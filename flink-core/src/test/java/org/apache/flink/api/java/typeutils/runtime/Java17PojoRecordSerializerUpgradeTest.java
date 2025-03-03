/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.api.java.typeutils.runtime;

import org.apache.flink.FlinkVersion;
import org.apache.flink.api.common.typeutils.TypeSerializerUpgradeTestBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** A {@link TypeSerializerUpgradeTestBase} for the {@link PojoSerializer}. */
class Java17PojoRecordSerializerUpgradeTest
        extends TypeSerializerUpgradeTestBase<
                Java17PojoRecordSerializerUpgradeTestSpecifications.RecordMigrationSetup
                        .RecordBeforeMigration,
                Java17PojoRecordSerializerUpgradeTestSpecifications.RecordMigrationVerifier
                        .RecordAfterSchemaUpgrade> {

    @Override
    public Collection<FlinkVersion> getMigrationVersions() {
        List<FlinkVersion> testVersions = new ArrayList<>();
        testVersions.add(FlinkVersion.v1_19);
        return testVersions;
    }

    public Collection<TestSpecification<?, ?>> createTestSpecifications(FlinkVersion flinkVersion)
            throws Exception {
        Collection<TestSpecification<?, ?>> testSpecifications = new ArrayList<>();
        testSpecifications.add(
                new TestSpecification<>(
                        "pojo-serializer-to-record",
                        flinkVersion,
                        Java17PojoRecordSerializerUpgradeTestSpecifications.PojoToRecordSetup.class,
                        Java17PojoRecordSerializerUpgradeTestSpecifications.PojoToRecordVerifier
                                .class));
        testSpecifications.add(
                new TestSpecification<>(
                        "pojo-serializer-record-migration",
                        flinkVersion,
                        Java17PojoRecordSerializerUpgradeTestSpecifications.RecordMigrationSetup
                                .class,
                        Java17PojoRecordSerializerUpgradeTestSpecifications.RecordMigrationVerifier
                                .class));
        return testSpecifications;
    }
}
