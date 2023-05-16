# chs-element-android

## Info

This repo builds mirror of [Element Android](https://github.com/vector-im/element-android) with custom home server.

Below is a step-by-step guide to create your mirror of [Element Android](https://github.com/vector-im/element-android) using this repository.

## Prepare

At first, you should have a Google Play Console account. You can create it [here](https://play.google.com/console/u/0/signup).

Once you have a Google Play Console account, open the main page of Google Play Console (All apps) and create an app in the right top corner. Follow the instructions there to create an app draft there.

Now copy/fork this repository to your GitHub account and follow the steps below to build a first version of your app.

## Create an upload key for Google Play

To upload aps to Google Play Console, you should sign them with the upload key.

Two of the ways to create an upload key (select any of these):

### With Android Studio

To create an upload key with Android Studio you need to have a buildable project.

1. Open Android Studio
2. Select "Create a project" / "New project" (or open any existing project, if you have any - skip the next step then)
3. Create a project with default settings
4. Wait for Gradle + indexing to complete (status bar at the bottom of Android Studio should disappear, takes several minutes)
5. Select "Build" on the top panel
6. Select "Generate Signed Bundle / APK"
7. Click "Next" on the first page
8. Click "Create new" under "Key Store Path"
9. In the field "Key store path" select a path to some folder + add your keystore name ("keystore.jks")
10. In the fields "Password" and "Confirm" under "Key store path" create a password to your key store consisting of latin letters and digits (do not use special symbols)
11. In the field "Alias" select a key alias (something like "upload1" should be fine)
12. In the fields "Password" and "Confirm" under "Alias" create a password to your key consisting of latin letters and digits (do not use special symbols)
13. Select validity (it have to be at least 25 years)
14. Enter your data in the Certificate section
15. Click "Ok"
16. If everything is done correctly, you have created a key store and a key
17. Now you can exit Android Studio and remove just created project. Note: do not remove your keystore (created in step 9) if you created it in this project.

Now provide the key store and the key to the GitHub runners. To do this:

1. Open your copy of this repository in browser
2. Open repository settings
3. Open "Secrets and variables" - "Actions"
4. Set the following 5 secrets by clicking on "New repository secret" for each secret:
   1. `KEYSTORE_CONTENT` - to generate this, run command `base64 path_to_your_keystore.jks -w 0` where `path_to_your_keystore.jks` is a path to keystore created in step 9 of the instrucion above
   2. `KEYSTORE_PASSWORD` - the password you created in the step 10 of the instruction above
   3. `KEYSTORE_NAME` - just set it to `keystore.jks`
   4. `KEY_ID` - the key alias you created in step 11 of the instruction above
   5. `KEY_PASSWORD` - the password you created in the step 12 of the instruction above

### With Keytool

**On Linux:**

1. Run the following commands in Terminal:

```
sudo apt install openjdk-19-jre-headless
mkdir upload_key_for_element
cd upload_key_for_element
keytool -genkey -v -keystore keystore.jks -storetype jks -alias UPLOAD1 -keyalg RSA -keysize 2048 -validity 10000 -storepass PASSWORD1 -keypass PASSWORD2
```

with `PASSWORD1` and `PASSWORD2` replaced by arbitrary strings, consisting of latin letters and digits (you can change `UPLOAD1` as well, but it is not necessary)

2. Provide the information that is asked of you ("First & Last Name", "Organizational Unit", "Organization", "City / Locality", "State / Province", "Country Code")
3. Type "Yes" to confirm it (when you are asked "Is ... correct?")
4. Run the command `base64 keystore.jks -w 0`

**On MacOS:**

1. If you don't have homebrew, run ```/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"```` in Terminal and follow the instrutions there. You will have to copy several commands and reopen Terminal.
2. Run the following commands in Terminal:

```
brew install openjdk
mkdir upload_key_for_element
cd upload_key_for_element
keytool -genkey -v -keystore keystore.jks -storetype jks -alias UPLOAD1 -keyalg RSA -keysize 2048 -validity 10000 -storepass PASSWORD1 -keypass PASSWORD2
```

with `PASSWORD1` and `PASSWORD2` replaced by arbitrary strings, consisting of latin letters and digits (you can change `UPLOAD1` as well, but it is not necessary)

3. Provide the information that is asked of you ("First & Last Name", "Organizational Unit", "Organization", "City / Locality", "State / Province", "Country Code")
4. Type "Yes" to confirm it (when you are asked "Is ... correct?")
5. Run the command `base64 keystore.jks -w 0`

**On Windows:**

1. If you don't have Chocolatey installed:
    1. Open Administrative Power Shell
    2. Run `Set-ExecutionPolicy AllSigned`
    3. Run
    ```Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))```
2. Run the following command in Administrative Power Shell:

```
choco install openjdk
```

3. Close Administrative Power Shell
4. Run this commands in any Terminal:

```
mkdir upload_key_for_element
cd upload_key_for_element
keytool -genkey -v -keystore keystore.jks -storetype jks -alias UPLOAD1 -keyalg RSA -keysize 2048 -validity 10000 -storepass PASSWORD1 -keypass PASSWORD2
```

with `PASSWORD1` and `PASSWORD2` replaced by arbitrary strings, consisting of latin letters and digits (you can change `UPLOAD1` as well, but it is not necessary)

5. Provide the information that is asked of you ("First & Last Name", "Organizational Unit", "Organization", "City / Locality", "State / Province", "Country Code")
6. Type "Yes" to confirm it (when you are asked "Is ... correct?")
7. Run the command `base64 keystore.jks -w 0`

**All OS (after the steps above):**

1. Open your copy of this repository in browser
2. Open repository settings
3. Open "Secrets and variables" - "Actions"
4. Set the following 5 secrets by clicking on "New repository secret" for each secret:
    1. `KEYSTORE_CONTENT` - output of the last step above in Terminal (in instruction for your OS)
    2. `KEYSTORE_PASSWORD` - the string you that you used to replace `PASSWORD1` above
    3. `KEYSTORE_NAME` - just set it to `keystore.jks`
    4. `KEY_ID` - `UPLOAD1` or the string you that you used to replace `UPLOAD1` above
    5. `KEY_PASSWORD` - the string you that you used to replace `PASSWORD2` above

## Creating a first release

*You cannot create first release automatically, because it requires more actions then just building an app. Mainly it is creating an app id (which requires new app to be uploaded to Google Play Console by hands) and providing some information about app to Google.*

Before building the first release, you should select app id for your app. Recommended one is your reversed domain name + ".element" (for example, "com.mycomp.element").

Add it to the repository secrets in your copy of this repository with name `APP_ID`.

Also add your home server as secret named `HOMESERVER`.

Now you should make your copy of this repository private in order to safely create the first version of this app.

1. Open your copy of this repository in browser
2. Open repository settings
3. In the "Danger Zone" section, change repository visibility to private (don't do anything if repository is already private)

To build the first version of your custom Element build do the following:

1. Open your copy of this repository in browser
2. Open actions
3. Select action "Execute Fastlane Command" on the left
4. Click "Run workflow"
5. Select command "BuildAAB"
6. "upload_artifact" should be selected
7. "version" should be the version of Element you would like to use as a first version of your Element mirror (for example, "1.5.30")
8. Run the workflow, it will take 20-30 minutes to complete (**It is possible that workflow will terminate with error "fastlane finished with errors", but with no errors generated. In this case, just rerun the whole workflow.**)
9. In the main page of the action (click on the action name to get there) you will find artifact `release.aab`
10. Download it to your device, unzip it (it can be wrapped in several folders) and remove it from artifacts

Once you have `.aab` file on your device but not in artifacts, you can make your copy of this repository public.

1. Open your copy of this repository in browser
2. Open repository settings
3. In the "Danger Zone" section, change repository visibility to public

Now you should upload this file to Google Play Console.

Element can be used in cars, but doing so requires a special agreement (you won't be able to upload the generated `.aab` without confirming it).

1. Open Google Play Console in browser
2. Select your newly created app
3. In "Release" section, open "Advanced settings" - "Form factors"
4. Click "Add form factor"
5. Select "Android Auto"
6. Follow the instructions on the Google Play Console

To upload the app to the internal testing, follow the following steps.

1. Open Google Play Console in browser
2. Select your newly created app
3. Open "Testing" - "Internal testing"
4. Click "Create new release"
5. Create new signing key, prefer default options (this is a key on your Google Play Console account for this app, not related to the key you created earlier)
6. Upload `.aab` file you created before to the section that asks you to upload `.aab` files
7. Click "Continue" in the right bottom corner
8. Do the actions Google Play Console requires you to do (if it does) and confirm an internal tesing release

Now open the main page of the app in the Google Play Console and follow the steps Google requires you to do (like specifying target audience, apps info and so on). You will have to provide an account for testers. It can be an account on "matrix.org", not on your server.

Once you are done with it, move the app from the internal testing to production and wait for Google to confirm your app.

## Creating a service account

If you would like to update your app automatically when new version of Element comes out, you should create a service account.

1. Open Google Play Console in browser
2. Click **Setup → API access**
3. Click the **Create new service account** button
4. Follow the **Google Cloud Platform** link in the dialog, which opens a new tab/window:
   1. Click the **CREATE SERVICE ACCOUNT** button at the top of the **Google Cloud Platform Console**
   2. Provide a `Service account name` and click **Create**
   3. Click **Select a role**, then find and select **Service Account User**, and proceed
   4. Click the **Done** button
   5. Click on the **Actions** vertical three-dot icon of the service account you just created
   6. Select **Manage keys** on the menu
   7. Click **ADD KEY -> Create New Key**
   8. Make sure **JSON** is selected as the Key type, and click **CREATE**
   9. Save the file on your computer when prompted and remember where it was saved to
5. Return to the **Google Play Console** tab, and click **DONE** to close the dialog
6. Click on **Grant Access** for the newly added service account at the bottom of the screen
7. Choose the permissions: *“create and edit draft apps”* and all from **RELEASE MANAGEMENT**: *“manage production releases”*, *“manage alpha&beta releases”*, *“manage alpha and beta test configuration”*
8. Click **Invite user**, then **Send invite** buttons
9. Select the **Users and permissions** on the left, your service account should be in the list of users, to the right of it you need to click the **Manage** button 
10. Click **Add app** and select `Element` from list, 
11. Click **Apply**
12. Click **Save changes** in the lower right corner and **Yes** in the window that opens

Now provide the service account info to GitHub runner:

1. Open your copy of this repository in browser
2. Open repository settings
3. Open "Secrets and variables" - "Actions"
4. Set the following 2 secrets by clicking on "New repository secret" for each secret:
    1. `PLAY_CONFIG_JSON_CONTENT` - to get this, run `base64 path_to_json.json -w 0`, where `path_to_json.json` is a path to json you created in step 4.9 above
    2. `PLAY_CONFIG_JSON_NAME` - name of this json file as you saved it

## Configuring updates

1. Open your copy of this repository in browser
2. Open repository settings
3. Open "Secrets and variables" - "Actions"
4. Open "Variables"
5. Click "New repository variable"
6. Create variable `days` with value `4` (once new Element version is released, your version would be generated `days` days after the official one - we don't want to publish our app before the official one)
7. Open Actions in your repository
8. Run "Check New Versions" with "Build the latest version anyway" on to verify that everything works as expected

You can enable/disable update checking on the action page of action "Check New Versions". 

## Other

You can change other [config setting](https://github.com/vector-im/element-android/blob/develop/vector-config/src/main/res/values/config.xml) (like changing the preffered Jitsi domain) in the step `Edit Config` of the `.github/workflows/execFastlane.yml`.
