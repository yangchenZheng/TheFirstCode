package com.example.zhengyangchen.amnesia.bean;

/**
 * Created by zhengyangchen on 2015/10/28.
 */
public class Alarms {
    /**
     * 主键
     */
    private int id;
    /**
     * 闹钟事件添加的时间
     */
    private long dateLong;
    /**
     * 闹钟响的时间
     */
    private long alarmsRunTime;
    /**
     * 闹钟事件描述
     */
    private String alarmsDesc;
    /**
     * 闹钟循环次数
     */
    private int alarmsCycleIndex;
    /**
     * 闹钟响的地点
     */
    private long alarmsAddress;
    /**
     * 闹钟是否启动
     */
    private int isAlarmsRun;

    public static final String TABLE_NAME = "tb_alarms";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE_LONG = "dateLong";
    public static final String COLUMN_ALARMS_RUN_TIME = "alarmsRunTime";
    public static final String COLUMN_ALARMS_DESC = "alarmsDesc";
    public static final String COLUMN_ALARMS_CYCLE_INDEX = "alarmsCycleIndex";
    public static final String COLUMN_ALARMS_ADDRESS = "alarmsAddress";
    public static final String COLUMN_IS_ALARMS_RUN = "isAlarmsRun";
    public static final int ALARMS_IS_RUNING = 1;
    public static final int ALARMS_IS_NOT_RUNING = 0;



    public Alarms() {
    }

    public Alarms(long dateLong, long alarmsRunTime, int isAlarmsRun, int alarmsCycleIndex, String alarmsDesc) {
        this.dateLong = dateLong;
        this.alarmsRunTime = alarmsRunTime;
        this.isAlarmsRun = isAlarmsRun;
        this.alarmsCycleIndex = alarmsCycleIndex;
        this.alarmsDesc = alarmsDesc;
    }

    public Alarms(int isAlarmsRun, long dateLong, long alarmsRunTime, String alarmsDesc, int alarmsCycleIndex, long alarmsAddress) {
        this.isAlarmsRun = isAlarmsRun;
        this.dateLong = dateLong;
        this.alarmsRunTime = alarmsRunTime;
        this.alarmsDesc = alarmsDesc;
        this.alarmsCycleIndex = alarmsCycleIndex;
        this.alarmsAddress = alarmsAddress;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDateLong() {
        return dateLong;
    }

    public void setDateLong(long dateLong) {
        this.dateLong = dateLong;
    }

    public long getAlarmsRunTime() {
        return alarmsRunTime;
    }

    public void setAlarmsRunTime(long alarmsRunTime) {
        this.alarmsRunTime = alarmsRunTime;
    }

    public String getAlarmsDesc() {
        return alarmsDesc;
    }

    public void setAlarmsDesc(String alarmsDesc) {
        this.alarmsDesc = alarmsDesc;
    }

    public int getAlarmsCycleIndex() {
        return alarmsCycleIndex;
    }

    public void setAlarmsCycleIndex(int alarmsCycleIndex) {
        this.alarmsCycleIndex = alarmsCycleIndex;
    }

    public long getAlarmsAddress() {
        return alarmsAddress;
    }

    public void setAlarmsAddress(long alarmsAddress) {
        this.alarmsAddress = alarmsAddress;
    }

    public int getIsAlarmsRun() {
        return isAlarmsRun;
    }

    public void setIsAlarmsRun(int isAlarmsRun) {
        this.isAlarmsRun = isAlarmsRun;
    }
}
