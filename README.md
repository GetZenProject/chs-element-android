# chs-element-android

## Info

This repo builds mirror of [Element Android](https://github.com/vector-im/element-android) with custom home server.

Below is a step-by-step guide to create your mirror of [Element Android](https://github.com/vector-im/element-android) using this repository.

## Prepare

At first, you should have a Google Play Console account. You can create it [here](https://play.google.com/console/u/0/signup).

Once you have a Google Play Console account, open the main page of Google Play Console (All Apps) and create an app in the right top corner. Follow the instructions there to create an app draft there.

Now copy/fork this repository to your GitHub account and follow the steps below to build a first version of your app.

## Create an upload key for Google Play

To upload aps to Google Play Console, you should sign them with the upload key.

To create an upload key with Android Studio you need to have a buildable project.

1. Open Android Studio
2. Select "Create a project"
3. Create a project with default settings
4. Wait for Gradle + indexing to complete (status bar at the bottom of Android Studio should disappear, takes several minutes)
5. Select "Build" on the top panel
6. Select "Generate Signed Bundle / APK"
7. Click "Next" on the first page
8. Click "Create new" under "Key Store Path"

![keystore](https://github.com/GetZenProject/chs-element-android/assets/54836310/e6fc1a28-95a3-48ce-8073-567259d4b19d)

9. In the field "Key store path" select a path to some folder + add your keystore name ("keystore.jks")
10. In the fields "Password" and "Confirm" under "Key store path" create a password to your key store consisting of latin letters and digits (do not use special symbols)
11. In the field "Alias" select a key alias (something like "upload1" should be fine)
12. In the fields "Password" and "Confirm" under "Alias" create a password to your key consisting of latin letters and digits (do not use special symbols)
13. Select validity (it have to be at least 25 years)
14. Enter your data in the Certificate section
15. Click "Ok"
16. If everything is done correctly, you have created a key store and a key
17. Now you can exit Android Studio and remove just created project. Note: do not remove your keystore (created in step 9) if you created it in this project.

There are other ways to create a key store and a key, but we recommend to use this one.

Now provide the key store and the key to the GitHub runners. To do this:

1. Open your copy of this repository
2. Open repository settings
3. Open "Secrets and variables" - "Actions"
4. Set the following 5 secrets by clicking on "New repository secret" for each secret:
   - `KEYSTORE_CONTENT` - to generate this, run command `base64 path_to_your_keystore.jks` where `path_to_your_keystore.jks" is a path to keystore created in step 9 of the instrucion above
   - `KEYSTORE_PASSWORD` - the password you created in the step 10 of the instruction above
   - `KEYSTORE_NAME` - how you would like to name your keystore on runner ("keystore.jks" for example) - any name consisting of latin letters and digits with ".jks" file format
   - `KEY_ID` - the key alias you created in step 11 of the instruction above
   - `KEY_PASSWORD` - the password you created in the step 12 of the instruction above

## Creating a first release

Before building the first release, you should select app id for your app. Recommended one is your reversed domain name + ".element" (for example, "com.mycomp.element").

Add it to the repository secrets in your copy of this repository with name `APP_ID`.

Now you should make your copy of this repository private in order to safely create the first version of this app.

1. Open your copy of this repository
2. Open repository settings
3. In the "Danger Zone" section, change repository visibility to private (don't do anything if repository is already private)

To build the first version of your custom Element build do the following:

1. Open your copy of this repository
2. Open actions
3. Select action "Execute Fastlane Command" on the left
4. Click "Run workflow"
5. Select command "BuildAAB"
6. "upload_artifact" should be selected
7. "version" should be the version of Element you would like to use as a first version of your Element mirror (for example, "1.5.30")
8. Run the workflow, it will take 20-30 minutes to complete
9. In the main page of the action (click on the action name to get there) you will find artifact `release.aab`
10. Download it to your device, unzip it (it can be wrapped in several folders) and remove it from artifacts

Once you have `.aab` file on your device but not in artifacts, you can make your copy of this repository public.

1. Open your copy of this repository
2. Open repository settings
3. In the "Danger Zone" section, change repository visibility to public

Now you should this file to Google Play Console.

1. Open Google Play Console
2. Select your newly created app
3. Open "Testing" - "Internal testing"
4. Click "Create new release"
5. Create new signing key, prefer default options (this is a key on your Google Play Console account for this app, not related to the key you created above)
6. Upload `.aab` file you created before to the section that asks you to upload `.aab` files
7. Click `Continue` in the right bottom corner
8. Do the actions Google Play Console requires you to do (if it does) and confirm an internal tesing release

How open the main page of the app in the Google Play Console and follow the steps Google requires you to do.

Once you are done with it, move the app from the internal testing to releases and wait for Google to confirm your app.

## Creating a service account

## Configuring updates

