package com.redrockwork.overrdie.bihu.developtools;

public class Time {
    private long timeMillis;
    private String timeString;
    private int everyMonthDays[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31, 32};
    private String timeZoneId = "中国";
    private int timeZoneNumber = 8;

    /**
     * 构造方法1
     *
     * @param timeMillis
     */
    public Time(long timeMillis) {
        this.timeMillis = timeMillis;

    }

    /**
     * 构造方法2
     *
     * @param timeString
     */
    public Time(String timeString) {
        this.timeString = timeString;
    }

    /**
     * getter方法
     *
     * @return
     */
    public long getTimeMillis() {
        return timeMillis;
    }

    public String getTimeString() {
        return timeString;
    }


    /**
     * 将时间戳转化成时间格式
     *
     * @param l1
     * @return
     */
    public String timeMillisChanageToDate(long l1) {
        int year = 1970;
        int clockMonth = 1;
        long clockSecond = l1 % 60;
        long clockMin = (l1 - clockSecond) / 60 % 60;
        long clockHour = (l1 - clockSecond - clockMin * 60) / 3600 % 24 + timeZoneNumber;
        long sumDay = (l1 - clockSecond - clockMin * 60 - clockHour * 3600) / (3600 * 24) + 1;
        for (; sumDay >= 365; year++) {
            if (year % 100 == 0) {
                if (year % 4 == 0) {
                    sumDay -= 366;
                } else {
                    sumDay -= 365;
                }
            } else if (year % 4 == 0) {
                sumDay -= 366;
            } else {
                sumDay -= 365;
            }
        }
        sumDay += 1;
//        System.out.println(sumDay);
        februaryDay(year);
        for (int i = 0; i < everyMonthDays.length; i++) {
            sumDay -= everyMonthDays[i];
            clockMonth += 1;
            if (sumDay <= everyMonthDays[i + 1])
                break;
        }
        String str = year + "-" + clockMonth + "-" + sumDay + "/" + clockHour + ":" + clockMin + ":" + clockSecond;
        timeString = str;
        return str;
    }

    /**
     * 将日期格式转化成时间戳
     *
     * @return
     */
    public long dateChangeToTimeMillis() {
        String[] str = timeString.split("/");
//        System.out.println(str[0] + " " + str[1]);
        String[] date = str[0].split("\\-");
        String[] time = str[1].split(":");
        //用于测试
//        for (int i = 0; i < date.length; i++) {
//            System.out.println(date[i]);
//        }
//        for (int i = 0; i < time.length; i++) {
//            System.out.println(time[i]);
//        }
        int clockYear = Integer.parseInt(date[0]);
        int clockMonth = Integer.parseInt(date[1]);
        int clockDay = Integer.parseInt(date[2]);
        int clockHour = Integer.parseInt(time[0]);
        int clockMin = Integer.parseInt(time[1]);
        int clockSecond = Integer.parseInt(time[2]);
        long sumDay = 0, sumMin = 0;
        long timeMillis = 0;
        for (; clockYear > 1970; clockYear--) {
            if (clockYear % 100 == 0) {
                if (clockYear % 4 == 0) {
                    sumDay += 366;
                } else {
                    sumDay += 365;
                }
            } else if (clockYear % 4 == 0) {
                sumDay += 366;
            } else {
                sumDay += 365;
            }
        }
        februaryDay(clockYear);
//        System.out.println(everyMonthDays[1]);
        for (int i = 0; i < clockMonth - 1; i++) {
            sumDay += everyMonthDays[clockMonth - 2 - i];
        }
        sumDay += clockDay - 1;
        sumMin = clockMin + (clockHour - timeZoneNumber) * 60;
        timeMillis = sumDay * 3600 * 24 + sumMin * 60 + clockSecond;
        this.timeMillis = timeMillis;
        return timeMillis;
    }

    /**
     * 判断当前年的二月份日期数
     *
     * @param clockYear
     */
    private void februaryDay(int clockYear) {
        if (clockYear % 100 == 0) {
            if (clockYear % 4 == 0) {
                everyMonthDays[1] = 29;
            } else {
                everyMonthDays[1] = 28;
            }

        } else if (clockYear % 4 == 0) {
            everyMonthDays[1] = 29;
        } else {
            everyMonthDays[1] = 28;
        }
    }

    public void showTime() {
        System.out.println(timeZoneId + "时间:" + this.timeString + " 秒数:" + timeMillis);
    }

    public void setTimeZone(String country) {
        switch (country) {
            case "China":
                timeZoneNumber = 8;
                this.timeZoneId = "中国";
                break;
            case "America":
                timeZoneNumber = -4;
                this.timeZoneId = "美国";
                break;
            case "England":
                timeZoneNumber = 0;
                this.timeZoneId = "英国";
                break;
            case "Janpan":
                timeZoneNumber = 9;
                this.timeZoneId = "日本";
                break;
            default:
                timeZoneNumber = 0;
                this.timeZoneId = "格林尼治";
                break;
        }

    }
}
