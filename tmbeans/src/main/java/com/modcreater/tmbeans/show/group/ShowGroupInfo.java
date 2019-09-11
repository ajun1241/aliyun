package com.modcreater.tmbeans.show.group;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-06
 * Time: 15:23
 */
@Data
public class ShowGroupInfo {

    private Long groupId;

    private String groupPicture;
    private String groupName;
    private String groupUnit;

    private Long membersNum;
    private List<Map<String,Object>> membersInfo;

    private Long groupScale;
    private Long groupNature;
    private String groupPresentation;
    private String crateDate;

    private int role;

}
