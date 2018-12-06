package com.bosnet.ngemart.libgen;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.bosnet.ngemart.libgen.Dummy.DeviceId;
import com.bosnet.ngemart.libgen.Dummy.DeviceIdContract;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DbClientTest extends ApplicationTestCase<Application> {
    private static DbClient dbClient = null;
    private List<Contract> contractList = new ArrayList<>();
    private DbConfig dbConfig = new DbConfig() {
        @Override
        public List<Contract> getListContract() throws Exception {
            List<Contract> contractList = new ArrayList<>();
            contractList.add(new DeviceIdContract());

            return contractList;
        }

        @Override
        public String getDbName() {
            return null;
        }

        @Override
        public int getDbVersion() {
            return 0;
        }
    };

    public DbClientTest() {
        super(Application.class);
    }

    private static DeviceId getData(String id) {
        DeviceId deviceId = new DeviceId();
        deviceId.id = id;
        deviceId.RowKey = new UUID(1000, 1000).randomUUID().toString();
        deviceId.UID = deviceId.RowKey;
        deviceId.amount = 10000d;
        deviceId.price = 500f;
        deviceId.quantity = 10;
        deviceId.updated = new Date();
        String data = "8888888";
        deviceId.image = data.getBytes();
        return deviceId;
    }

    public void test_Find() throws Exception {

        DbClient dbClient = getDbClient();

        String id = GetRandomId();
        DeviceId deviceIdDataExpected = getData(id);

        try {
            dbClient = getDbClient();
            dbClient.BeginTransaction();
            dbClient.Insert(deviceIdDataExpected);
            dbClient.CommitTransaction();
        } finally {
            dbClient.ReleaseConnection();
        }

        DeviceId deviceIdActual = dbClient.Find(DeviceId.class, id);
        Assert.assertEquals(deviceIdDataExpected.toString(), deviceIdActual.toString());
    }

    public void testDelete() throws Exception {
        String id = GetRandomId();
        DeviceId deviceId = getData(id);

        try {
            dbClient = getDbClient();
            dbClient.BeginTransaction();
            dbClient.Insert(deviceId);
            dbClient.CommitTransaction();
        } finally {
            dbClient.ReleaseConnection();
        }

        try {
            dbClient = getDbClient();
            dbClient.BeginTransaction();
            dbClient.Delete(deviceId);
            dbClient.CommitTransaction();
        } finally {
            dbClient.ReleaseConnection();
        }

        DeviceId deviceIdResult = dbClient.Find(DeviceId.class, id);
        assertEquals(null, deviceIdResult);
    }

    private String GetRandomId() {
        return new UUID(10000, 10000).randomUUID().toString() + Thread.currentThread().getId() + Math.random();
    }

    public void test_Query() throws Exception {

        String id = GetRandomId();
        DeviceId deviceId = getData(id);

        try {
            dbClient = getDbClient();
            dbClient.BeginTransaction();
            dbClient.Insert(deviceId);
            dbClient.CommitTransaction();
        } finally {
            dbClient.ReleaseConnection();
        }

        DeviceId deviceIdResult = dbClient.Find(DeviceId.class, id);
        Assert.assertEquals(deviceIdResult.id, id);
    }

    public void test_Find_DataNotFound() throws Exception {
        DbClient dbClient = getDbClient();
        DeviceId deviceId = dbClient.Find(DeviceId.class, "XXX");
        Assert.assertEquals(deviceId, null);
    }

    public void test_Insert() throws Exception {
        String id = GetRandomId();
        DeviceId deviceId = getData(id);
        deviceId.RowKey = "Insert";

        try {
            dbClient = getDbClient();
            dbClient.BeginTransaction();
            dbClient.Insert(deviceId);
            dbClient.CommitTransaction();
        } finally {
            dbClient.ReleaseConnection();
        }

        DeviceId deviceIdResult = dbClient.Find(DeviceId.class, id);

        assertEquals(deviceIdResult.RowKey, "Insert");
    }

    public void test_Insert_memberNull() {
        try {

            try {
                dbClient = getDbClient();
                dbClient.BeginTransaction();
                DeviceId deviceId = getData(null);
                dbClient.Insert(deviceId);
                dbClient.CommitTransaction();
            } finally {
                dbClient.ReleaseConnection();
            }

            assertFalse(true);
        } catch (Exception e) {
            Assert.assertEquals("java.lang.NullPointerException: id", e.toString());
        }
    }

    public void test_InsertAll_NullList() {
        try {
            DbClient dbClient = getDbClient();

            try {
                dbClient.BeginTransaction();
                dbClient.InsertAll(null);
                dbClient.CommitTransaction();
            } finally {
                dbClient.ReleaseConnection();
            }
        } catch (Exception e) {
            assertEquals("java.lang.NullPointerException: listData", e.toString());
        }
    }

    DbClient getDbClient() throws Exception {
        contractList.add(new DeviceIdContract());
        DbClient dbClient = new DbClient(getContext(), dbConfig);
        dbClient.OpenConnection();
        return dbClient;
    }

    public void test_Insert_nested_transaction() throws Exception {
        String id = GetRandomId();
        String id2 = GetRandomId();
        DeviceId deviceId = getData(id);

        try {
            dbClient = getDbClient();
            dbClient.BeginTransaction();
            dbClient.Insert(deviceId);
            new NestedClass2().Save(id2);
            dbClient.CommitTransaction();
        } finally {
            dbClient.ReleaseConnection();
        }

        DeviceId deviceResult1 = dbClient.Find(DeviceId.class, id);
        assertEquals(id, deviceResult1.id);
        DeviceId deviceResult2 = dbClient.Find(DeviceId.class, id2);
        assertEquals(id2, deviceResult2.id);
    }

    public void test_Insert_nested_transaction_error() throws Exception {
        String id = GetRandomId();
        DeviceId deviceId = getData(id);

        try {
            dbClient = getDbClient();
            dbClient.BeginTransaction();
            dbClient.Insert(deviceId);
            new NestedClass().Save();
            dbClient.CommitTransaction();
        } catch (Exception e) {

        } finally {
            dbClient.ReleaseConnection();
        }

        DeviceId deviceResult1 = dbClient.Find(DeviceId.class, id);
        assertEquals(null, deviceResult1);
    }

    public void test_InsertAll_NullMember() {
        try {
            DbClient dbClient = getDbClient();

            List<DeviceId> deviceIdList = new ArrayList<>();
            DeviceId deviceId = getData("001");

            deviceIdList.add(deviceId);

            deviceId = getData("002");
            deviceId.RowKey = null;
            deviceIdList.add(deviceId);

            try {
                dbClient = getDbClient();
                dbClient.BeginTransaction();
                dbClient.InsertAll(deviceIdList);
                dbClient.CommitTransaction();
            } finally {
                dbClient.ReleaseConnection();
            }

        } catch (Exception e) {
            assertEquals("java.lang.NullPointerException: RowKey", e.toString());
        }
    }

    public void test_InsertAll() throws Exception {

        String id1 = GetRandomId();
        String id2 = GetRandomId();
        String id3 = GetRandomId();

        List<DeviceId> deviceIdList = new ArrayList<>();
        DeviceId deviceId = getData(id1);
        deviceIdList.add(deviceId);

        deviceId = getData(id2);
        deviceIdList.add(deviceId);

        deviceId = getData(id3);
        deviceIdList.add(deviceId);

        try {
            dbClient = getDbClient();
            dbClient.BeginTransaction();
            dbClient.InsertAll(deviceIdList);
            dbClient.CommitTransaction();
        } finally {
            dbClient.ReleaseConnection();
        }

    }

    public void test_Insert_null() {
        try {
            DbClient dbClient = getDbClient();
            try {
                dbClient.BeginTransaction();
                dbClient.Insert(null);
                dbClient.CommitTransaction();
            } finally {
                dbClient.ReleaseConnection();
            }

            assertFalse(true);
        } catch (Exception e) {
            assertEquals(e.toString(), "java.lang.NullPointerException: data");
        }
    }

    public void test_Save() throws Exception {
        String id = GetRandomId();
        DeviceId deviceId = getData(id);

        try {
            dbClient = getDbClient();
            dbClient.BeginTransaction();
            dbClient.Save(deviceId);
            dbClient.CommitTransaction();
        } finally {
            dbClient.ReleaseConnection();
        }

        try {
            dbClient = getDbClient();
            dbClient.BeginTransaction();
            deviceId.RowKey = "Luis Ginanjar";
            dbClient.Save(deviceId);
            dbClient.CommitTransaction();
        } finally {
            dbClient.ReleaseConnection();
        }


        DeviceId deviceIdResult = dbClient.Find(DeviceId.class, id);
        assertEquals("Luis Ginanjar", deviceIdResult.RowKey);
    }

    public void test_Save_withError() throws Exception {

        String id = GetRandomId();
        DeviceId deviceId;

        try {
            dbClient = getDbClient();
            dbClient.BeginTransaction();
            deviceId = getData(id);
            dbClient.Insert(deviceId);
            throw new Exception("");
        } catch (Exception e) {
        } finally {
            dbClient.ReleaseConnection();
        }

        DeviceId deviceResult = dbClient.Find(DeviceId.class, id);
        assertEquals(null, deviceResult);
    }

    public void test_Delete() throws Exception {

        String id = GetRandomId();
        DeviceId deviceId = getData(id);

        try {
            dbClient = getDbClient();
            dbClient.BeginTransaction();

            dbClient.Insert(deviceId);
            dbClient.CommitTransaction();
        } finally {
            dbClient.ReleaseConnection();
        }

        try {
            dbClient = getDbClient();
            dbClient.BeginTransaction();
            dbClient.Delete(deviceId);
            dbClient.CommitTransaction();
        } finally {
            dbClient.ReleaseConnection();
        }

        DeviceId deviceResult = dbClient.Find(DeviceId.class, id);
        assertEquals(null, deviceResult);
    }

    //region CRUD ProductDetail_Test

    public void testGetDateTime() throws Exception {
        contractList.add(new DeviceIdContract());
        DbClient dbClient = new DbClient(getContext(), dbConfig);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 17);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DATE, 2);
        cal.set(Calendar.MONTH, 2);
        cal.set(Calendar.YEAR, 2016);

        Date date = cal.getTime();
        String timeActual = dbClient.getDateTime(date);
        String timeExpected = "2016-03-02 17:30:00";

        assertEquals(timeExpected, timeActual);
    }

    public void testGetDateFromString() throws Exception {

        DbClient dbClient = new DbClient(getContext(), dbConfig);
        String time = "2016-03-02 17:30:00";
        Date actualDate = dbClient.getDateFromString(time);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 17);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DATE, 2);
        cal.set(Calendar.MONTH, 2);
        cal.set(Calendar.YEAR, 2016);
        Date dateExpected = cal.getTime();

        assertEquals(dateExpected, actualDate);
    }


    /**
     * Creator : Bintang
     * Purpose : Testing of class model which has more than 1 or even 2 primary keys.
     */



    //endregion

    class NestedClass {
        public void Save() throws Exception {
            try {
                dbClient = getDbClient();
                dbClient.BeginTransaction();
                DeviceId deviceId = getData("002");
                dbClient.Save(deviceId);

                throw new Exception("");
            } finally {
                dbClient.ReleaseConnection();
            }
        }
    }

    class NestedClass2 {
        public void Save(String id) throws Exception {
            DbClient dbClient = getDbClient();
            try {
                dbClient.BeginTransaction();
                DeviceId deviceId = getData(id);
                dbClient.Save(deviceId);
                dbClient.CommitTransaction();
            } finally {
                dbClient.ReleaseConnection();
            }
        }
    }
}