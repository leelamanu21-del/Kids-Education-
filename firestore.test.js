const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function validProfile(uid) {
  const pastTime = new Date(Date.now() - 1000);
  return {
    userId: uid,
    childName: "Aarav",
    preferredMode: "SATPIN",
    preferredLanguage: "HINGLISH",
    totalStars: 3,
    unlockedLevelOrder: 2,
    streakDays: 1,
    createdAt: pastTime,
    updatedAt: pastTime,
  };
}

test("Unauthenticated user cannot read or write profile or letter progress", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("letter_progress")
      .get()
  );
});

test("Authenticated owner can create and read their own learner profile", async () => {
  const aliceCtx = testEnv.authenticatedContext(ALICE_UID);
  const aliceDb = aliceCtx.firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(validProfile(ALICE_UID))
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Shadow update with ghost field is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const profileWithGhost = { ...validProfile(ALICE_UID), isAdmin: true };
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set(profileWithGhost)
  );
});

test("Cross-user profile and letter_progress access is denied", async () => {
  const pastTime = new Date(Date.now() - 1000);
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    const adminDb = ctx.firestore();
    await adminDb.collection("users").doc(BOB_UID).set(validProfile(BOB_UID));
    await adminDb
      .collection("users")
      .doc(BOB_UID)
      .collection("letter_progress")
      .doc("S")
      .set({
        userId: BOB_UID,
        letter: "S",
        starsEarned: 3,
        mastered: true,
        practiceCount: 2,
        bestQuizScore: 100,
        createdAt: pastTime,
        updatedAt: pastTime,
      });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(
    aliceDb
      .collection("users")
      .doc(BOB_UID)
      .collection("letter_progress")
      .get()
  );
});

test("Owner can write and list own letter_progress with valid schema, invalid letter rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const pastTime = new Date(Date.now() - 1000);
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("letter_progress")
      .doc("S")
      .set({
        userId: ALICE_UID,
        letter: "S",
        starsEarned: 3,
        mastered: true,
        practiceCount: 1,
        bestQuizScore: 100,
        createdAt: pastTime,
        updatedAt: pastTime,
      })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("letter_progress")
      .get()
  );

  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("letter_progress")
      .doc("BAD")
      .set({
        userId: ALICE_UID,
        letter: "BAD",
        starsEarned: 5,
        mastered: true,
        practiceCount: 1,
        bestQuizScore: 100,
        createdAt: pastTime,
        updatedAt: pastTime,
      })
  );
});

test("Owner can create, update, and list stage_progress while cross-user and invalid stages fail", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const pastTime = new Date(Date.now() - 2000);

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("stage_progress")
      .doc("level2_blender")
      .set({
        userId: ALICE_UID,
        stageId: "level2_blender",
        starsEarned: 2,
        completionsCount: 1,
        createdAt: pastTime,
        updatedAt: pastTime,
      })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("stage_progress")
      .doc("level2_blender")
      .update({
        starsEarned: 4,
        completionsCount: 2,
        updatedAt: new Date(Date.now() - 1000),
      })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("stage_progress")
      .get()
  );

  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("stage_progress")
      .get()
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("stage_progress")
      .doc("level8_class5")
      .set({
        userId: ALICE_UID,
        stageId: "level8_class5",
        starsEarned: 5,
        completionsCount: 2,
        createdAt: pastTime,
        updatedAt: pastTime,
      })
  );

  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("stage_progress")
      .doc("unknown_stage")
      .set({
        userId: ALICE_UID,
        stageId: "unknown_stage",
        starsEarned: 10,
        completionsCount: 1,
        createdAt: pastTime,
        updatedAt: pastTime,
      })
  );
});
