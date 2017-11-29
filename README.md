# Dear Udacity Reviewer
This app is already in the Android store and has an active userbase. It has a 4.8/5 star ratting.I 
released it well over a year ago and updated it for the Udacity project submission.

PLEASE DO NOT DO ANY DESTRUCTIVE DATA ACTIONS IN THE APP! It might affect other users!

You can view the store listing here:

https://play.google.com/store/apps/details?id=com.playposse.thomas.lindenmayer

The topic of the app are Lindenmayer Systems. As this is a mathematical niche subject, you may not
be familiar with them. The app includes introductory information. If you want to inform yourself
through a neutral third-party side, here is the Wiki link:
https://en.wikipedia.org/wiki/L-system


# Exception/Error Handling
The code handles user errors. However, app errors are thrown as exceptions instead of being handled.
I monitor Fabric/Crashlytics to fix exceptions.

It's a better user experience to have the app crash on one user (and have it fixed quickly) rather
than the app showing all users pretty error messages.


#Legacy Code
The app has some code to handle legacy cases that may not be obvious from looking only at the
current code. For example, older installs may have JSON files where the user saved custom data. The
app no longer has code to create those files. However, there is code to read those old files and
convert them to the current storage paradigm.


#Signing Key
Obviously, it would be a major security and Google store policy violation to hand out the actual
key for this app. Udacity's goal with the key rubric seems to be to ascertain that a student has
the skills to submit the app to the app store. I've more than demonstrated that by building a 
userbase. However, to be double sure that the rubric is fulfilled, there is a dummy key store
included: "useless-for-udacity.jks". The password is "12345678". The key alias is "key".


#Color Scheme
The default thought for a color scheme with Material Design is to use bold colors. In this case,
the app uses almost exclusively non-colors (white, gray, and black aren't colors). The reason is
that the goal of the app is to create visual renderings. Any color of the app itself would detract
or influence the end results. That's why visual creator apps like PhotoShop avoid the use of colors.


#Bragging
Seeing L-Systems for the first time, the algorithm may not seem special. Before the latest update,
the app was the fastest L-System rendering app in the app store. For the last update, I used the
new Android Studio 3.0 profiler to optimize the algorithm even more. It's now faster by another
scale. The code has some comments to explain the reasons for non-obvious unusual code that's
highly performance tuned.


# Coding Standard
- I am a conscientious dissenter of prefixing field names with the letter 'm'. This app follows
the Google coding standard, not that of the Android team. There are plenty of intelligent arguments
on the Internet by leaders of the community why the prefixes are bad. If you are looking for a
pointer to get started on this topic, here you go: 
http://jakewharton.com/just-say-no-to-hungarian-notation/

- The Udacity coding standard asks for all public methods to be commented. I did NOT comment
obvious methods. For example, a 'newInstance' method on a fragment is probably going to create a
new instance of the fragment.


# Notes
- You will find some unused methods in the utility classes. I've created a few utility classes that
I use on my projects. I copy them from one project to the next. So, I didn't create them from
scratch for this one.

- I've used stackoverflow and the Android documentation quite a bit. There are individual lines of
code or short snippets copy-pasted and modified in the code all over the place. For example, I've
lifted the code to check for WiFi and adjusted it for my needs.
