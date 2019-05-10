const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

const token = 'dMmBFPcc6oY:APA91bGIFCJ4xHqC5NyS3UKXNAv4lQqXx9x0osAaNFEfZF6RFaGft2v_Jc3yZm1qYVQHaQ16MRSE3pPmEKXWgS8EVDmJ0KZSELXsVX-CTqOhzvcwv7MV_sBfEGJrYO5DsrxHwDaqtlpF'

exports.registerNotification = functions.firestore
    .document('brews/{brew}')
    .onWrite((change, context) => {
    // Get an object with the current document value.
    // If the document does not exist, it has been deleted.
    const document = change.after.exists ? change.after.data() : null;

    // Get an object with the previous document value (for update or delete)
    const oldDocument = change.before.data();

    // Get document details
        const name = document.recipe.name
        const stage = document.stage;
        const now = new Date();
        var endDate;
        var message;

        switch (stage) {
            case "PRIMARY":
            case "PAUSED":
                message = "Primary Fermentation";
                break;
            case "SECONDARY":
            case "COMPLETE":
                message = "Secondary Fermentation";
                break;
        }
        endDate = new Date(document.endDate);

        var days;

        // Copy date parts of the timestamps, discarding the time parts.
        var one = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        var two = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate());

        // Do the math.
        var millisecondsPerDay = 1000 * 60 * 60 * 24;
        var millisBetween = two.getTime() - one.getTime();
        days = Math.ceil(millisBetween / millisecondsPerDay);

        if (days > 0) {
            message += (" is ending in " + days + " days.");
        } else {
            message += " is done!"
        }

        // Build Notification from document details
        const payload = {
            notification: {
                title: name,
                body: message
            }
        };

        // Log for debugging purposes
        console.log("endDate: " + endDate.getDate() + " now: " + now.getDate() + " days: " + days);

        // Fire notification to user
        return admin.messaging().sendToDevice(token, payload);

});

