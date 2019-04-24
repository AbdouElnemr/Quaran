package com.elnemr.quaran.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuranModel implements Serializable {

    public class Api {

        @SerializedName("Sora")
        @Expose
        private String sora;
        @SerializedName("link")
        @Expose
        private String link;
        @SerializedName("Reader_Name")
        @Expose
        private String readerName;
        @SerializedName("pageNumber")
        @Expose
        private Integer pageNumber;
        @SerializedName("Sora_Type")
        @Expose
        private String soraType;
        @SerializedName("Sora_Number")
        @Expose
        private Integer soraNumber;

        public String getSora() {
            return sora;
        }

        public void setSora(String sora) {
            this.sora = sora;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getReaderName() {
            return readerName;
        }

        public void setReaderName(String readerName) {
            this.readerName = readerName;
        }

        public Integer getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(Integer pageNumber) {
            this.pageNumber = pageNumber;
        }

        public String getSoraType() {
            return soraType;
        }

        public void setSoraType(String soraType) {
            this.soraType = soraType;
        }

        public Integer getSoraNumber() {
            return soraNumber;
        }

        public void setSoraNumber(Integer soraNumber) {
            this.soraNumber = soraNumber;
        }

    }

}
