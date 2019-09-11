package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.*;
import com.modcreater.tmbeans.show.group.ShowGroupEventMsg;
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
     * 根据团队Id查询关系表
     * @param groupId
     * @return
     */
    List<GroupRelation> queryGroupRelation(String groupId);

    /**
     * 查询团队信息
     * @param groupId
     * @return
     */
    ShowGroupInfo getMyGroupInfo(String groupId);

    /**
     * 查询团队成员Id
     * @param groupId
     * @return
     */
    List<String> getMembersId(String groupId);

    /**
     * 根据type获取团队默认头像地址
     * @param groupNature
     * @return
     */
    String getGroupDefaultHeadImgUrl(String groupNature);

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

    /**
     * 获取管理员数量
     * @param groupId
     * @return
     */
    Long getManagerNum(String groupId);

    /**
     * 获取管理员信息
     * @param groupId
     * @return
     */
    List<Map<String, Object>> getManagerInfo(String groupId);

    /**
     * 获取创建者信息
     * @param groupId
     * @return
     */
    Map<String, Object> getCreatorInfo(String groupId);

    /**
     * 修改成员身份
     * @param groupId
     * @param memberId
     * @param memberLevel
     * @return
     */
    int updateMemberLevel(@Param("groupId") String groupId,@Param("memberId") String memberId,@Param("memberLevel") int memberLevel);

    /**
     * 获取成员身份
     * @param groupId
     * @param userId
     * @return
     */
    int getMemberLevel(@Param("groupId")String groupId,@Param("memberId") String userId);

    /**
     * 移除团队成员
     * @param groupId
     * @param memberId
     * @return
     */
    int removeMember(@Param("groupId")String groupId, @Param("memberId")String memberId);

    /**
     * 根据成员身份查询成员信息
     * @param groupId
     * @param i
     * @return
     */
    List<Map<String ,Object>> queryGroupMemberInfoByLevel(@Param("groupId") String groupId,@Param("memberLevel") int memberLevel);

    /**
     * 查询团队成员
     * @param groupId
     * @param memberId
     * @return
     */
    GroupRelation queryGroupMember(String groupId, String memberId);

    /**
     * 保存加入团队验证消息
     * @param groupSystemMsg
     * @return
     */
    int saveGroupMsg(GroupSystemMsg groupSystemMsg);

    /**
     * 查询消息状态
     * @param groupMsgId
     * @return
     */
    GroupSystemMsg getGroupMsgById(String groupMsgId);

    /**
     * 保存团队申请
     * @param groupValidation
     * @return
     */
    int saveValidationContent(GroupValidation groupValidation);

    /**
     * 更改团队创建者
     * @param groupId
     * @param memberId
     * @return
     */
    int changeCreator(@Param("groupId") String groupId,@Param("memberId") String memberId);

    /**
     * 删除团队内所有成员
     * @param groupId
     * @return
     */
    int removeAllMember(String groupId);

    /**
     * 删除团队
     * @param groupId
     * @return
     */
    int removeGroup(String groupId);

    /**
     * 查询验证处理详情
     * @param groupValidationId
     * @return
     */
    GroupValidation getGroupValidation(Long groupValidationId);

    /**
     * 修改验证状态
     * @param groupValidationId
     * @param processState
     * @return
     */
    int updGroupValidation(Long groupValidationId, String processState,Long processDate,String processBy);

    /**
     * 查询团队验证消息
     * @param userId
     * @return
     */
    List<GroupSystemMsg> queryApplyMsgList(String userId);

    /**
     * 查询团队历史事件消息
     * @param groupId
     * @return
     */
    List<ShowGroupEventMsg> getGroupEventMsg(String groupId);
}
