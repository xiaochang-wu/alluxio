/*
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */

package alluxio.client.file.options;

import alluxio.AuthenticatedUserRule;
import alluxio.CommonTestUtils;
import alluxio.Configuration;
import alluxio.ConfigurationTestUtils;
import alluxio.Constants;
import alluxio.PropertyKey;
import alluxio.client.AlluxioStorageType;
import alluxio.client.UnderStorageType;
import alluxio.client.WriteType;
import alluxio.client.file.policy.FileWriteLocationPolicy;
import alluxio.client.file.policy.LocalFirstPolicy;
import alluxio.client.file.policy.RoundRobinPolicy;
import alluxio.security.authorization.Mode;
import alluxio.util.CommonUtils;
import alluxio.wire.TtlAction;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

/**
 * Tests for the {@link OutStreamOptions} class.
 */
public class OutStreamOptionsTest {
  @Rule
  public AuthenticatedUserRule mRule = new AuthenticatedUserRule("test");

  /**
   * Tests that building an {@link OutStreamOptions} with the defaults works.
   */
  @Test
  public void defaults() throws IOException {
    AlluxioStorageType alluxioType = AlluxioStorageType.STORE;
    UnderStorageType ufsType = UnderStorageType.SYNC_PERSIST;
    Configuration.set(PropertyKey.USER_BLOCK_SIZE_BYTES_DEFAULT, "64MB");
    Configuration.set(PropertyKey.USER_FILE_WRITE_TYPE_DEFAULT, WriteType.CACHE_THROUGH.toString());
    Configuration.set(PropertyKey.USER_FILE_WRITE_TIER_DEFAULT, Constants.LAST_TIER);

    OutStreamOptions options = OutStreamOptions.defaults();

    Assert.assertEquals(alluxioType, options.getAlluxioStorageType());
    Assert.assertEquals(64 * Constants.MB, options.getBlockSizeBytes());
    Assert.assertTrue(options.getLocationPolicy() instanceof LocalFirstPolicy);
    Assert.assertEquals("test", options.getOwner());
    Assert.assertEquals("", options.getGroup());
    Assert.assertEquals(Mode.defaults().applyFileUMask(), options.getMode());
    Assert.assertEquals(Constants.NO_TTL, options.getTtl());
    Assert.assertEquals(TtlAction.DELETE, options.getTtlAction());
    Assert.assertEquals(ufsType, options.getUnderStorageType());
    Assert.assertEquals(WriteType.CACHE_THROUGH, options.getWriteType());
    Assert.assertEquals(Constants.LAST_TIER, options.getWriteTier());
    ConfigurationTestUtils.resetConfiguration();
  }

  /**
   * Tests getting and setting fields.
   */
  @Test
  public void fields() {
    Random random = new Random();
    long blockSize = random.nextLong();
    FileWriteLocationPolicy locationPolicy = new RoundRobinPolicy();
    String owner = CommonUtils.randomAlphaNumString(10);
    String group = CommonUtils.randomAlphaNumString(10);
    Mode mode = new Mode((short) random.nextInt());
    long ttl = random.nextLong();
    int writeTier = random.nextInt();
    WriteType writeType = WriteType.NONE;

    OutStreamOptions options = OutStreamOptions.defaults();
    options.setBlockSizeBytes(blockSize);
    options.setLocationPolicy(locationPolicy);
    options.setOwner(owner);
    options.setGroup(group);
    options.setMode(mode);
    options.setTtl(ttl);
    options.setTtlAction(TtlAction.FREE);
    options.setWriteTier(writeTier);
    options.setWriteType(writeType);

    Assert.assertEquals(blockSize, options.getBlockSizeBytes());
    Assert.assertEquals(locationPolicy, options.getLocationPolicy());
    Assert.assertEquals(owner, options.getOwner());
    Assert.assertEquals(group, options.getGroup());
    Assert.assertEquals(mode, options.getMode());
    Assert.assertEquals(ttl, options.getTtl());
    Assert.assertEquals(TtlAction.FREE, options.getTtlAction());
    Assert.assertEquals(writeTier, options.getWriteTier());
    Assert.assertEquals(writeType.getAlluxioStorageType(), options.getAlluxioStorageType());
    Assert.assertEquals(writeType.getUnderStorageType(), options.getUnderStorageType());
  }

  @Test
  public void equalsTest() throws Exception {
    CommonTestUtils.testEquals(OutStreamOptions.class);
  }
}
