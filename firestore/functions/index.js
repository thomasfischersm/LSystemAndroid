const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.updateLikeCount = functions.firestore
    .document('likes/{likeId}').onWrite((event) => {

        // Determine operation type.
        var exists = event.data.exists;
        var hasExisted = (event.data.previous != null) && event.data.previous.exists;
        var isCreate = (exists && !hasExisted);
        var isDelete = (!exists && hasExisted);
        console.log('Determined operations: ' + exists + ' ' + hasExisted + ' ' + isCreate + ' ' + isDelete);

        // Determine rule set id.
        if (isCreate) {
            console.log('isCreate');
            var ruleSetId = event.data.data().ruleSetId;
        } else if (isDelete) {
            console.log('isDelete');
            var ruleSetId = event.data.previous.data().ruleSetId;
        } else {
            console.log('Unpexected operation! ' + exists + ' ' + hasExisted);
        }
        console.log('Selected rule set is: ' + ruleSetId)

        // Update like count.
        return admin.firestore()
            .collection('likes')
            .where('ruleSetId', '==', ruleSetId)
            .get()
            .then(query => {
                console.log('like count is: ' + query.size);
                var likeCount = query.size;
                return admin.firestore()
                            .collection('ruleSets')
                            .doc(ruleSetId)
                            .set({likeCount: likeCount}, { merge: true });
            });
    });
