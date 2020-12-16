package com.courtapi.aws.s3;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class S3URITest {
  @Test
  void smoke() {
    var uri = new S3URI("s3://bucket/path/to/key");
    assertEquals("bucket", uri.getBucket());
    assertEquals("path/to/key", uri.getKey());

    // fragment should be strippedj
    uri = new S3URI("s3://bucket/key#fragment");
    assertEquals("bucket", uri.getBucket());
    assertEquals("key", uri.getKey());

    // query and fragment are both stripped
    uri = new S3URI("s3://bucket/key?query=foo&bar=baz#fragment");
    assertEquals("bucket", uri.getBucket());
    assertEquals("key", uri.getKey());

    // scheme is not case sensitive
    uri = new S3URI("S3://bucket/key");
    assertEquals("bucket", uri.getBucket());
    assertEquals("key", uri.getKey());
  }

  @Test
  void illegalSchemeThrowsException() {
    var exception = assertThrows(IllegalArgumentException.class, () -> new S3URI("http://bucket/path"));
    assertTrue(exception.getMessage().contains("Only s3 schemes are supported"));
  }

  @Test
  void missingSchemeThrowsException() {
    var exception = assertThrows(IllegalArgumentException.class, () -> new S3URI("//bucket/path"));
    assertTrue(exception.getMessage().contains("Invalid S3 URI: no scheme"));

    exception = assertThrows(IllegalArgumentException.class, () -> new S3URI("://bucket/path"));
    assertTrue(exception.getMessage().contains("Invalid S3 URI: no scheme"));
  }

  @Test
  void missingBucketOrKeyThrowsException() {
    var exception = assertThrows(IllegalArgumentException.class, () -> new S3URI("s3://"));
    assertTrue(exception.getMessage().contains("Bucket or key missing in URI"));

    exception = assertThrows(IllegalArgumentException.class, () -> new S3URI("s3://bucket"));
    assertTrue(exception.getMessage().contains("Bucket or key missing in URI"));

    exception = assertThrows(IllegalArgumentException.class, () -> new S3URI("s3:////"));
    assertTrue(exception.getMessage().contains("Invalid S3 bucket"));

    exception = assertThrows(IllegalArgumentException.class, () -> new S3URI("s3://bucket/"));
    assertTrue(exception.getMessage().contains("Invalid S3 key"));
  }
}