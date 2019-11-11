package com.modcreater.tmutils.store;

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
                Long startTime1 = Long.valueOf(o1.getStartTime());
                Long startTime2 = Long.valueOf(o2.getStartTime());
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

    public static void sortPromoteSalesInfoWithoutStatus(List<ShowPromoteSalesInfo> infos){
        infos.sort(new Comparator<ShowPromoteSalesInfo>() {
            @Override
            public int compare(ShowPromoteSalesInfo o1, ShowPromoteSalesInfo o2) {
                Long startTime1 = Long.valueOf(o1.getStartTime());
                Long startTime2 = Long.valueOf(o2.getStartTime());
                return startTime1.compareTo(startTime2);
            }
        });

    }
}
