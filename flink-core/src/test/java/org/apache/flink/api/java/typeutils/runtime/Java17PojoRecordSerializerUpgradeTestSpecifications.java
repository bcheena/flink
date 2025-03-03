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
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.ClassRelocator;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.TypeSerializerMatchers;
import org.apache.flink.api.common.typeutils.TypeSerializerSchemaCompatibility;
import org.apache.flink.api.common.typeutils.TypeSerializerUpgradeTestBase;
import org.apache.flink.api.java.typeutils.TypeExtractor;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertSame;

/** A {@link TypeSerializerUpgradeTestBase} for the {@link PojoSerializer}. */
class Java17PojoRecordSerializerUpgradeTestSpecifications {

    public static final class PojoToRecordSetup
            implements TypeSerializerUpgradeTestBase.PreUpgradeSetup<
                    PojoToRecordSetup.PojoBeforeUpgrade> {

        @ClassRelocator.RelocateClass("TestPojoToRecord")
        @SuppressWarnings("WeakerAccess")
        public static class PojoBeforeUpgrade {
            public int id;
            public String name;

            public PojoBeforeUpgrade() {}

            public PojoBeforeUpgrade(int id, String name) {
                this.id = id;
                this.name = name;
            }
        }

        @Override
        public TypeSerializer<PojoBeforeUpgrade> createPriorSerializer() {
            TypeSerializer<PojoBeforeUpgrade> serializer =
                    TypeExtractor.createTypeInfo(PojoBeforeUpgrade.class)
                            .createSerializer(new ExecutionConfig());
            assertSame(PojoSerializer.class, serializer.getClass());
            return serializer;
        }

        @Override
        public PojoBeforeUpgrade createTestData() {
            return new PojoBeforeUpgrade(911108, "Gordon");
        }
    }

    public static final class PojoToRecordVerifier
            implements TypeSerializerUpgradeTestBase.UpgradeVerifier<
                    PojoToRecordVerifier.PojoAfterUpgrade> {

        @ClassRelocator.RelocateClass("TestPojoToRecord")
        @SuppressWarnings("WeakerAccess")
        public record PojoAfterUpgrade(int id, String name) {}

        @Override
        public TypeSerializer<PojoAfterUpgrade> createUpgradedSerializer() {
            TypeSerializer<PojoAfterUpgrade> serializer =
                    TypeExtractor.createTypeInfo(PojoAfterUpgrade.class)
                            .createSerializer(new ExecutionConfig());
            assertSame(PojoSerializer.class, serializer.getClass());
            return serializer;
        }

        @Override
        public Matcher<PojoAfterUpgrade> testDataMatcher() {
            return is(new PojoAfterUpgrade(911108, "Gordon"));
        }

        @Override
        public Matcher<TypeSerializerSchemaCompatibility<PojoAfterUpgrade>>
                schemaCompatibilityMatcher(FlinkVersion version) {
            return TypeSerializerMatchers.isCompatibleAsIs();
        }
    }

    public static final class RecordMigrationSetup
            implements TypeSerializerUpgradeTestBase.PreUpgradeSetup<
                    RecordMigrationSetup.RecordBeforeMigration> {

        @ClassRelocator.RelocateClass("TestRecordMigration")
        @SuppressWarnings("WeakerAccess")
        public record RecordBeforeMigration(int id, String name) {}

        @Override
        public TypeSerializer<RecordBeforeMigration> createPriorSerializer() {
            TypeSerializer<RecordBeforeMigration> serializer =
                    TypeExtractor.createTypeInfo(RecordBeforeMigration.class)
                            .createSerializer(new ExecutionConfig());
            assertSame(PojoSerializer.class, serializer.getClass());
            return serializer;
        }

        @Override
        public RecordBeforeMigration createTestData() {
            return new RecordBeforeMigration(911108, "Gordon");
        }
    }

    public static final class RecordMigrationVerifier
            implements TypeSerializerUpgradeTestBase.UpgradeVerifier<
                    RecordMigrationVerifier.RecordAfterSchemaUpgrade> {

        @ClassRelocator.RelocateClass("TestRecordMigration")
        @SuppressWarnings("WeakerAccess")
        public record RecordAfterSchemaUpgrade(String name, int age, String newField) {}

        @Override
        public TypeSerializer<RecordAfterSchemaUpgrade> createUpgradedSerializer() {
            TypeSerializer<RecordAfterSchemaUpgrade> serializer =
                    TypeExtractor.createTypeInfo(RecordAfterSchemaUpgrade.class)
                            .createSerializer(new ExecutionConfig());
            assertSame(PojoSerializer.class, serializer.getClass());
            return serializer;
        }

        @Override
        public Matcher<RecordAfterSchemaUpgrade> testDataMatcher() {
            return is(new RecordAfterSchemaUpgrade("Gordon", 0, null));
        }

        @Override
        public Matcher<TypeSerializerSchemaCompatibility<RecordAfterSchemaUpgrade>>
                schemaCompatibilityMatcher(FlinkVersion version) {
            return TypeSerializerMatchers.isCompatibleAfterMigration();
        }
    }
}
