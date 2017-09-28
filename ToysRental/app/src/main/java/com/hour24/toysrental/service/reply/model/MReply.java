package com.hour24.toysrental.service.reply.model;

/**
 * Created by 장세진 on 2016-08-13.
 */
public class MReply {

    private String replySeq;
    private String memberSeq;
    private String memberId;
    private String memberName;
    private String contentSeq;
    private String contentCd;
    private String content;
    private String source;
    private String deleteYn;
    private String intDt;

    public MReply() {

    }

    public String getContentCd() {
        return contentCd;
    }

    public void setContentCd(String contentCd) {
        this.contentCd = contentCd;
    }

    public String getContentSeq() {
        return contentSeq;
    }

    public void setContentSeq(String contentSeq) {
        this.contentSeq = contentSeq;
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

    public String getReplySeq() {
        return replySeq;
    }

    public void setReplySeq(String replySeq) {
        this.replySeq = replySeq;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}
