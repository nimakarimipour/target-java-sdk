/*
 * Copyright 2019 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * NOTE: This is an auto generated file. Do not edit directly.
 */
package com.adobe.target.delivery.v1.model;

import javax.annotation.Nullable;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Application {
    @JsonProperty("id")@Nullable
    private String id;

    @JsonProperty("name")@Nullable
    private String name;

    @JsonProperty("version")@Nullable
    private String version;

    public Application id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Application ID. If not specified - all activities with any applicationId will be evaluated. If specified - only
     * activities with the matching applicationId will be evaluated.
     *
     * @return id
     **/

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Application name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Application name. If not specified - all activities with any applicationName will be evaluated. If specified -
     * only activities with specified applicationName will be evaluated.
     *
     * @return name
     **/

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Application version(String version) {
        this.version = version;
        return this;
    }

    /**
     * Application version If not specified - all activities with any applicationVersion will not be evaluated. If
     * specified - only activities with specific applicationVersion will be evaluated.
     *
     * @return version
     **/

    @Nullable
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Application application = (Application) o;
        return Objects.equals(this.id, application.id) &&
                Objects.equals(this.name, application.name) &&
                Objects.equals(this.version, application.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, version);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Application {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    version: ").append(toIndentedString(version)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(@Nullable Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

