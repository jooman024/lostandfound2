package icia.js.lostandfound.mapper;

import java.util.List;

import icia.js.lostandfound.beans.FoundCommentsBean;
import icia.js.lostandfound.beans.LostCommentsBean;

public interface commentInter {
    void insLostComment(LostCommentsBean lostComment);
    List<LostCommentsBean> lostcommentList(LostCommentsBean lostComment);
    void insFoundComment(FoundCommentsBean foundComment);
    List<FoundCommentsBean> foundcommentList(FoundCommentsBean foundComment);
}
