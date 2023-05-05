default_platform(:android)

platform :android do

  desc "Build AAB Release File"
  lane :buildAAB do
    gradle(task: "bundle",
      flavor: "Gplay",
      build_type: "Release",
      print_command: false,
      properties: {
        "signing.element.storePath" => "./SUBSTITUTE_KEYSTORE_NAME",
        "signing.element.storePassword" => "SUBSTITUTE_KEYSTORE_PASSWORD",
        "signing.element.keyId" => "SUBSTITUTE_KEY_ID",
        "signing.element.keyPassword" => "SUBSTITUTE_KEY_PASSWORD",
      },
      package_name: "SUBSTITUTE_APP_ID",
    )
  end

  desc "Deploy Metadata"
  lane :deployMeta do
    upload_to_play_store(
      json_key: "./SUBSTITUTE_JSON_KEY_FILE",
      track: "internal",
      package_name: "SUBSTITUTE_APP_ID",
      skip_upload_apk: true,
      skip_upload_aab: true,
      skip_upload_images: true,
      skip_upload_screenshots: true,
      skip_upload_changelogs: true,
      validate_only: false,
    )
  end

  desc "Validate Key"
  lane :validateKey do
    validate_play_store_json_key(
      json_key: "./SUBSTITUTE_JSON_KEY_FILE",
    )
  end
	
end