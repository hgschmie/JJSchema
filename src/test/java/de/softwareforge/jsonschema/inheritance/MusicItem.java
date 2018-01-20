/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.softwareforge.jsonschema.inheritance;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class MusicItem extends BaseItem {

    private BigDecimal price;
    private String artistName;
    private String releaseYear;

    @JsonProperty
    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @JsonProperty
    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    @JsonProperty
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
