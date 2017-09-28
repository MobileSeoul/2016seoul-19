package com.hour24.toysrental.service.timeline.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by 장세진 on 2016-08-11.
 */
public class MTimeLine implements Serializable {

    private String boardSeq = "";
    private String boardCd = "";
    private String memberSeq = "";
    private String content = "";
    private String source = "";
    private String deleteYn = "";
    private String placeCd = "";
    private String placeName = "";
    private String intDt = "";
    private String udtDt = "";

    private String authCd = "";
    private String memberId = "";
    private String memberName = "";

    private String likeCt = "";

    private ArrayList<MPicture> listPictures;

    public MTimeLine() {

    }

    public String getBoardCd() {
        return boardCd;
    }

    public void setBoardCd(String boardCd) {
        this.boardCd = boardCd;
    }

    public String getBoardSeq() {
        return boardSeq;
    }

    public void setBoardSeq(String boardSeq) {
        this.boardSeq = boardSeq;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDeleteYn() {
        return deleteYn;
    }

    public void setDeleteYn(String deleteYn) {
        this.deleteYn = deleteYn;
    }

    public String getIntDt() {
        return intDt;
    }

    public void setIntDt(String intDt) {
        this.intDt = intDt;
    }

    public String getMemberSeq() {
        return memberSeq;
    }

    public void setMemberSeq(String memberSeq) {
        this.memberSeq = memberSeq;
    }

    public String getPlaceCd() {
        return placeCd;
    }

    public void setPlaceCd(String placeCd) {
        this.placeCd = placeCd;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUdtDt() {
        return udtDt;
    }

    public void setUdtDt(String udtDt) {
        this.udtDt = udtDt;
    }

    public String getAuthCd() {
        return authCd;
    }

    public void setAuthCd(String authCd) {
        this.authCd = authCd;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getLikeCt() {
        return likeCt;
    }

    public void setLikeCt(String likeCt) {
        this.likeCt = likeCt;
    }

    public ArrayList<MPicture> getListPictures() {
        return listPictures;
    }

    public void setListPictures(ArrayList<MPicture> listPictures) {
        this.listPictures = listPictures;
    }
}

