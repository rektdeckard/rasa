const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

const token = 'eAPfozHcTWQ:APA91bGhUF9SOFasFS-ALwRMguU7C0DJ71bLsuWT2YPHKYUblwDRqs6xVcMq3pRJOomgNvyl6DIx_XfrE9llmLMMEu19ipDgskbirP041e9m0_UcE3Lq38lJPZJ4EW7-whpSYP5nHzLD'

exports.registerNotification = functions.firestore
    .document('brews/{brew}')
    .onWrite((change, context) => {
      // Get an object with the current document value.
      // If the document does not exist, it has been deleted.
      const document = change.after.exists ? change.after.data() : null;

      // Get an object with the previous document value (for update or delete)
      const oldDocument = change.before.data();

      // Get document details
      const name = document.recipe.name;
      const stage = document.stage;
      const now = new Date();
      var endDate = null;
      var message = null;

      if (stage === 'PRIMARY' ) {
        endDate = new Date(document.secondaryStartDate);
        message = "Primary Fermentation";
      } else if (stage === 'SECONDARY') {
        endDate = new Date(document.endDate);
        message = "Secondary Fermentation";
      }

      // Build Notification from document details
      const payload = {
        notification: {
          title: name,
          body: message + " is ending in " + (endDate.getDate() - now.getDate()) + " days."
        }
      };

      // Log for debugging purposes
      console.log("endDate: " + endDate.getDate() + " now: " + now.getDate());

      // Fire notification to user
      return admin.messaging().sendToDevice(token, payload);

    });
