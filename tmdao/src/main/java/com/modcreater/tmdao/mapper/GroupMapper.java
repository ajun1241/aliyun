package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.GroupInfo;
import com.modcreater.tmbeans.pojo.GroupPermission;
import com.modcreater.tmbeans.pojo.GroupRelation;
import com.modcreater.tmbeans.show.group.ShowGroupInfo;
import com.modcreater.tmbeans.show.group.ShowMyGroup;
import com.modcreater.tmbeans.vo.GroupInfoVo;
import com.modcreater.tmbeans.vo.group.UpdateGroupInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/6 9:05
 */
@Mapper
public interface GroupMapper {
    /**
     * 查询我的所有团队
     * @param userId
     * @param role
     * @return
     */
    List<ShowMyGroup> getMyGroup(@Param("userId") String userId, @Param("role") int role);

    /**
     * 查询我创建的团队数量
     * @param userId
     * @return
     */
    int getMyCreatedGroupNum(String userId);

    /**
     * 创建一个团队
     * @param groupInfoVo
     * @return
     */
    Long createGroup(GroupInfoVo groupInfoVo);

    /**
     * 关系表添加创建者
     * @param userId
     * @param groupId
     * @return
     */
    int addCreator(@Param("userId") String userId,@Param("groupId") Long groupId);

    /**
     * 查询团队权限表
     * @param userId
     * @return
     */
    GroupPermission getGroupUpperLimit(String userId);

    /**
     * 根据Id查询团队详情
     * @param groupId
     * @return
     */
    GroupInfo queryGroupInfo(String groupId);

    /**
     * 创建用户群组权限
     * @param userId
     * @param groupUpperLimit
     * @return
     */
    int addGroupPermission(@Param("userId") String userId, @Param("groupUpperLimit") Long groupUpperLimit);

    /**
     * 创建成员
     * @param memberId
     * @param groupId
     * @return
     */
    int createMember(@Param("memberId") String memberId,@Param("groupId") Long groupId);

    /**
     * 查询团队信息
     * @param groupId
     * @return
     */
    ShowGroupInfo getMyGroupInfo(String groupId);

    /**
     * 查询团队成员
     * @param groupId
     * @return
     */
    List<Long> getMembersInfo(String groupId);

    /**
     * 根据type获取团队默认头像地址
     * @param groupNature
     * @return
     */
    String getGroupDefultHeadImgUrl(String groupNature);

    /**
     * 修改团队信息(单个修改)
     * @param groupId
     * @param updateType
     * @param value
     * @return
     */
    int updateGroupInfo(@Param("groupId") String groupId,@Param("updateType") String updateType,@Param("value") String value);

    /**
     * 查询团队所有默认头像地址
     * @return
     */
    List<String> getAllGroupDefultHeadImgUrls();
}
