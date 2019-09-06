package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.GroupInfo;
import com.modcreater.tmbeans.pojo.GroupPermission;
import com.modcreater.tmbeans.pojo.GroupRelation;
import com.modcreater.tmbeans.show.group.ShowMyGroup;
import com.modcreater.tmbeans.vo.GroupInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
}
