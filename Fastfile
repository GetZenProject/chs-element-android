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
      }
    )
  end

  desc "Download from Gplay"
  lane :download do
    download_from_play_store()
  end

  desc "Validate Key"
  lane :validateKey do
    validate_play_store_json_key(
      json_key_path: './SUBSTITUTE_JSON_KEY_FILE',
    )
  end
	
end