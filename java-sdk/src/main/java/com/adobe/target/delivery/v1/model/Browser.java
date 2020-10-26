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
public class Browser {
    @JsonProperty("host")@Nullable
    private String host;

    @JsonProperty("webGLRenderer")@Nullable
    private String webGLRenderer;

    public Browser host(String host) {
        this.host = host;
        return this;
    }

    /**
     * Current web page host.
     *
     * @return host
     **/

    @Nullable
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Browser webGLRenderer(String webGLRenderer) {
        this.webGLRenderer = webGLRenderer;
        return this;
    }

    /**
     * This is an optional field, added to help with device detection using device atlas.
     *
     * @return webGLRenderer
     **/

    @Nullable
    public String getWebGLRenderer() {
        return webGLRenderer;
    }

    public void setWebGLRenderer(String webGLRenderer) {
        this.webGLRenderer = webGLRenderer;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Browser browser = (Browser) o;
        return Objects.equals(this.host, browser.host) &&
                Objects.equals(this.webGLRenderer, browser.webGLRenderer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, webGLRenderer);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Browser {\n");
        sb.append("    host: ").append(toIndentedString(host)).append("\n");
        sb.append("    webGLRenderer: ").append(toIndentedString(webGLRenderer)).append("\n");
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

