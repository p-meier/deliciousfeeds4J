/*
 * Copyright (c) 2013 by Patrick Meier.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.delicious.deliciousfeeds4J.json;

import com.delicious.deliciousfeeds4J.beans.Tag;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A custom deserializer for the Tag-bean!
 *
 * @author Patrick Meier
 */
public class JacksonTagDeserializer extends JsonDeserializer<Set<Tag>> {

    @Override
    public Set<Tag> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        final Map<String, Integer> tagMap = jsonParser.readValueAs(new TypeReference<Map<String, Integer>>() {
        });

        //Build the set
        final Set<Tag> tags = new TreeSet<Tag>();

        for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
            final Tag tag = new Tag();
            tag.setName(entry.getKey());
            tag.setCount(entry.getValue());

            tags.add(tag);
        }

        return tags;
    }
}
