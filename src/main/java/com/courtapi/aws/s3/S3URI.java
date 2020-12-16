package com.courtapi.aws.s3;

import lombok.Getter;
import lombok.NonNull;

// this is a good candidate to open source
// *sigh* Amazon didn't port AmazonS3URI from v1 of the SDK over to v2.
// If they ever rectify this situation, we can drop this class.
// See: https://github.com/aws/aws-sdk-java-v2/issues/272
@Getter
public class S3URI {
  @NonNull
  private final String location;
  private String bucket;
  private String key;

  S3URI(final String location) {
    this.location = location;

    var schemeSplit = location.split("://", -1);
    if (schemeSplit.length != 2 || schemeSplit[0].isBlank())
      throw new IllegalArgumentException("Invalid S3 URI: no scheme: " + location);

    var scheme = schemeSplit[0];
    if (!scheme.equalsIgnoreCase("s3"))
      throw new IllegalArgumentException("Only s3 schemes are supported for URI: " + location);

    var bucketAndKey = schemeSplit[1].split("/", 2);

    if (bucketAndKey.length != 2)
      throw new IllegalArgumentException("Bucket or key missing in URI: " + location);
    if (bucketAndKey[0].isBlank())
      throw new IllegalArgumentException("Invalid S3 bucket: " + bucketAndKey[0]);
    if (bucketAndKey[1].isBlank())
      throw new IllegalArgumentException("Invalid S3 key: " + bucketAndKey[1]);

    this.bucket = bucketAndKey[0];

    // Strip away query and fragment if they exist.
    var path = bucketAndKey[1];
    path = path.split("\\?", -1)[0];
    path = path.split("#", -1)[0];

    this.key = path;
  }
}