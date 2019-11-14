package com.modcreater.tmutils.store;

import com.modcreater.tmbeans.show.goods.ShowGetUpdatePromoteSalesGoodsList;
import com.modcreater.tmbeans.show.store.ShowPromoteSalesInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-11-11
 * Time: 16:14
 */
public class StoreUtils {

    public static void sortPromoteSalesInfo(List<ShowPromoteSalesInfo> infos){
        infos.sort(new Comparator<ShowPromoteSalesInfo>() {
            @Override
            public int compare(ShowPromoteSalesInfo o1, ShowPromoteSalesInfo o2) {
                Long startTime1 = o1.getStartTime();
                Long startTime2 = o2.getStartTime();
                return startTime1.compareTo(startTime2);
            }
        });
        infos.sort(new Comparator<ShowPromoteSalesInfo>() {
            @Override
            public int compare(ShowPromoteSalesInfo o1, ShowPromoteSalesInfo o2) {
                Integer status1 = Integer.valueOf(o1.getStatus());
                Integer status2 = Integer.valueOf(o2.getStatus());
                return status2.compareTo(status1);
            }
        });
    }

    public static void sortAllOverduePromoteSalesInfo(List<ShowPromoteSalesInfo> infos){
        infos.sort(new Comparator<ShowPromoteSalesInfo>() {
            @Override
            public int compare(ShowPromoteSalesInfo o1, ShowPromoteSalesInfo o2) {
                Long promoteType1 = Long.valueOf(o1.getPromoteType());
                Long promoteType2 = Long.valueOf(o2.getPromoteType());
                return promoteType2.compareTo(promoteType1);
            }
        });
        infos.sort(new Comparator<ShowPromoteSalesInfo>() {
            @Override
            public int compare(ShowPromoteSalesInfo o1, ShowPromoteSalesInfo o2) {
                Long startTime1 = o1.getStartTime();
                Long startTime2 = o2.getStartTime();
                return startTime1.compareTo(startTime2);
            }
        });
    }

    public static void sortGoodsListBySelectStatus(List<ShowGetUpdatePromoteSalesGoodsList> lists){
        lists.sort(new Comparator<ShowGetUpdatePromoteSalesGoodsList>() {
            @Override
            public int compare(ShowGetUpdatePromoteSalesGoodsList o1, ShowGetUpdatePromoteSalesGoodsList o2) {
                boolean b1 = o1.getSelectStatus();
                boolean b2 = o2.getSelectStatus();
                if (b1 ^ b2) {
                    return b1 ? -1 : 1;
                } else {
                    return 0;
                }
            }
        });
    }

    /*public static void main(String[] args) {
        ShowPromoteSalesInfo info1 = new ShowPromoteSalesInfo();
        info1.setPromoteSalesId(1L);
        info1.setPromoteType("3");
        info1.setStartTime("1");
        ShowPromoteSalesInfo info2 = new ShowPromoteSalesInfo();
        info2.setPromoteSalesId(2L);
        info2.setPromoteType("1");
        info2.setStartTime("2");
        ShowPromoteSalesInfo info3 = new ShowPromoteSalesInfo();
        info3.setPromoteSalesId(3L);
        info3.setPromoteType("2");
        info3.setStartTime("3");
        ShowPromoteSalesInfo info4 = new ShowPromoteSalesInfo();
        info4.setPromoteSalesId(4L);
        info4.setPromoteType("3");
        info4.setStartTime("3");
        ShowPromoteSalesInfo info5 = new ShowPromoteSalesInfo();
        info5.setPromoteSalesId(5L);
        info5.setPromoteType("1");
        info5.setStartTime("4");
        ShowPromoteSalesInfo info6 = new ShowPromoteSalesInfo();
        info6.setPromoteSalesId(6L);
        info6.setPromoteType("4");
        info6.setStartTime("8");
        List<ShowPromoteSalesInfo> list = new ArrayList<>();
        list.add(info1);
        list.add(info3);
        list.add(info2);
        list.add(info4);
        list.add(info6);
        list.add(info5);
        sortAllOverduePromoteSalesInfo(list);
        for (ShowPromoteSalesInfo info : list){
            System.out.println(info.getPromoteSalesId() + ":" + info.getPromoteType() + ":" + info.getStartTime());
        }
    }*/
}
