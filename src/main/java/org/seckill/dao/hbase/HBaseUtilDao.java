package org.seckill.dao.hbase;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import com.google.common.base.Optional;
import org.seckill.util.Md5Util;
import org.seckill.util.PropertiesUtils;


/**
 * @author zhoulingjiang
 * @Title: HBaseUtilDao.java
 */
public class HBaseUtilDao {
    private static Configuration configuration;
    private static Connection conn = null;
    public static final String DEFAULT_COLUMN_FAMILY="info";
    public static final String CODEUTF8="UTF-8";
    public static final  Properties  prop  = new Properties();  
    public static String getRowKey(long key,long time){
    	return	getRowKeyPrefix(key)+"_"+getRowKeySuffix(time);
    }
    public static String getRowKeyPrefix(long key){
    	return	Md5Util.getMd5_32(String.valueOf(key));
    }
    public static String getRowKeySuffix(long time){
    	return	String.valueOf(Long.MAX_VALUE-time);
    }
    static {
    	InputStream in=null;
        configuration = HBaseConfiguration.create();
        /*configuration.set("hbase.zookeeper.property.clientPort", "5181");
        configuration.set("hbase.zookeeper.quorum", "10.10.10.182,10.10.10.183,10.10.10.184");
        configuration.set("hbase.master", "10.10.10.180:9000");*/
          
		//读取属性文件a.properties
//	    in =  MemoryCache.class.getClassLoader().getResourceAsStream(Constant.DEVELOP_CONF_PATH);

        try {
//        	prop.load(in);
            Properties confProperties = PropertiesUtils.getProperties("jdbc.properties");
            String ip = confProperties.getProperty("hadoop.namenode.ip");
 
        	configuration.set("hbase.zookeeper.property.clientPort", confProperties.getProperty("zk.port"));
            configuration.set("hbase.zookeeper.quorum", confProperties.getProperty("zk.clusters"));
            configuration.set("hbase.master", confProperties.getProperty("hadoop.namenode.ip")+":9000");
            conn = ConnectionFactory.createConnection(configuration);
        } catch (IOException e) {
            conn = null;
            e.printStackTrace();
        }
    }

   
    /**
     * 获取HBase链接信息
     *
     * @return HBase链接
     * @throws IOException
     */
    public static Connection getConn() throws IOException {
        if (null == conn || conn.isClosed()) {
            Configuration conf = HBaseConfiguration.create();
            configuration.set("hbase.zookeeper.property.clientPort", prop.getProperty("zk.port"));
            configuration.set("hbase.zookeeper.quorum", prop.getProperty("zk.clusters"));
            configuration.set("hbase.master", prop.getProperty("hadoop.namenode.ip")+":9000");
            /*conf.addResource("core-site.xml");
            conf.addResource("hdfs-site.xml");
            conf.addResource("hbase-site.xml");*/
            
            conn = ConnectionFactory.createConnection(conf);
        }
        return conn;
    }

    /**
     * 获取 Admin
     *
     * @return Admin
     * @throws IOException
     */
    public static Admin getAdmin() throws IOException {
        Connection conn = getConn();
        Admin admin = conn.getAdmin();
        return admin;
    }

    /**
     * 获取Table
     *
     * @param tableName table名称
     * @return Table
     * @throws IOException
     */
    public static Table getHTable(TableName tableName) throws IOException {
        Connection conn = getConn();
        Table table = conn.getTable(tableName);
        table.getTableDescriptor().getTableName();
        return table;
    }

    /**
     * 关闭HBase链接
     *
     * @param conn
     */
    public static void closeConn(Connection conn) {
        if (null != conn && !conn.isClosed()) {
            try {
                conn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭Table
     *
     * @param table
     */
    public static void closeTable(Table table) {
        if (null != table) {
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭Admin
     *
     * @param admin
     */
    public static void closeAdmin(Admin admin) {
        if (admin != null) {
            try {
                admin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void truncateTable(String tableName) {
        Admin admin = null;
        try {
            admin = getAdmin();
            TableName tn = TableName.valueOf(tableName);
            HTableDescriptor tableDesc = new HTableDescriptor(tn);
            tableDesc.addFamily(new HColumnDescriptor("info".getBytes(CODEUTF8)));
            if (admin.tableExists(tn)) {
                admin.disableTable(tn);
                admin.deleteTable(tn);
            }
            admin.createTable(tableDesc);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAdmin(admin);
        }
    }

    public static boolean isTableAvailable(Admin admin, String tableName) throws IOException {
        return admin.isTableAvailable(TableName.valueOf(tableName));
    }

    public static void createTable(String tableName) {
        Admin admin = null;
        try {
            admin = getAdmin();
            if (!isTableAvailable(admin, tableName)) {
                HTableDescriptor hbaseTable = new HTableDescriptor(TableName.valueOf(tableName));
                hbaseTable.addFamily(new HColumnDescriptor(DEFAULT_COLUMN_FAMILY));
                admin.createTable(hbaseTable);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAdmin(admin);
        }
    }


    public static List<String> listTables() throws IOException {
        List<String> rs = new ArrayList<String>();
        Admin admin = getAdmin();

        HTableDescriptor[] HTableDescriptor = admin.listTables();

        for (HTableDescriptor desc : HTableDescriptor) {
            String tableName = desc.getTableName().getNameAsString();
            rs.add(tableName);
        }

        closeAdmin(admin);
        return rs;
    }

    public static Optional<String> getResultByColumn(String tableName, String rowKey,
                                                     String familyName, String columnName) {
    	  Optional<String> cellValue = Optional.absent();
    try{
    	
       Table table = conn.getTable(TableName.valueOf(tableName));
       
    	Get get = new Get(Bytes.toBytes(rowKey));
        // 获取指定列族和列修饰符对应的列
        get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
        Result result = table.get(get);

	        if (result != null && result.listCells() != null) {
	            for (Cell cell : result.listCells()) {
	                cellValue = Optional.of(Bytes.toString(cell.getValue()));
	            }
	        }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return cellValue;
    }

    public static void updateColumnValue(String tableName, String rowKey,
                                         String familyName, String columnName, String value)
            throws IOException {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName),
                Bytes.toBytes(value));
        table.put(put);
        System.out.println("update table Success!");
    }

    
    public static  void rangeScan(String tableName, 
    		String startRowKey,
    		String endRowKey,
    		boolean flag){
    	try{
    		Table table = conn.getTable(TableName.valueOf(tableName));
        	Scan scan = new Scan(); 
        	scan.setCaching(500); 
        	scan.setCacheBlocks(false);
        	scan.setStartRow(Bytes.toBytes(startRowKey));
        	if(flag){
        		scan.setStopRow(Bytes.toBytes(endRowKey+"0"));	
        	}else{
        		scan.setStopRow(Bytes.toBytes(endRowKey));	
        	}

        	ResultScanner resultScanner = table.getScanner(scan);	
        	System. out .println("Rowkey(行键)"+"    "+"Familiy(列族)"+"    "+"Quilifier(列)"+"    "+"Value(列值)"+"    "+"Timestamp(数据入库时间)");
        	 for  (Result r : resultScanner) {  
        	        for  (Cell cell : r.rawCells()) {  
        	          /* System. out .println(  
        	                    "   Rowkey : " +Bytes. toString (r.getRow())+  
        	                    "   Familiy: "+Bytes. toString (CellUtil.cloneFamily(cell))+
        	                    "   Quilifier : " +Bytes. toString (CellUtil.cloneQualifier(cell))+ 
        	                    "   Value : " +Bytes. toString (CellUtil. cloneValue (cell))+  
        	                    "   Timestamp : " +StatDateAndTimeUtil.getDateStrByMillitime(cell.getTimestamp())  
        	                   );  */
        	        	
        	        	System. out .println(  
        	                    Bytes. toString (r.getRow())+  
        	                    "       "+Bytes. toString (CellUtil.cloneFamily(cell))+
        	                    "             " +Bytes. toString (CellUtil.cloneQualifier(cell))+ 
        	                    "            " +Bytes. toString (CellUtil. cloneValue (cell))
//        	                    +"      " +StatDateAndTimeUtil.getDateStrByMillitime(cell.getTimestamp())
        	                   );  
        	       }  
        	   }  
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    }
    
    public static void deleteTable(String tableName, String colFamily, String column) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        List<Delete> listOfBatchDeletes = new ArrayList<Delete>();

        //Get timestamps
//        long starTS = 1492563600000l;
//        long endTS = 1492606800000l;

        long starTS = 1493023380000l;//2017-04-24 15:52
        long endTS = 1493023500000l;//2017-04-24 15:55

        Scan scan = new Scan();
        scan.setTimeRange(1493024974130l, 1493024974131l);
        scan.setCaching(500);
//        scan.setTimeStamp(1493024861088l);
        scan.addFamily(Bytes.toBytes(colFamily));

        ResultScanner resultScanner = table.getScanner(scan);
        for (Result scanResult : resultScanner) {
            Delete delete = new Delete(scanResult.getRow());

            listOfBatchDeletes.add(delete);
            if (listOfBatchDeletes.size() >= 1) {
                table.delete(listOfBatchDeletes);
                listOfBatchDeletes.clear();
            }
        }

        try {
            table.close();
            System.err.println("delete successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //HBaseUtilDao.deleteTable("device_deviceusidrecorddetails", "info", "");
    	try{
    		//List tablenames=listTables();
    		//System.out.println(tablenames.toString());
    		//Optional<String>  value=getResultByColumn("t1","rowkey001","f1","col1");
    		//System.out.println(value.get());
    		//updateColumnValue("t1","rowkey002","f1","col1","row_valu03");
    		
    		
    		//createTable(htableName);
    		//long timelong=StatDateAndTimeUtil.getCurrentDate().getTime();
    		//System.out.println(timelong);
    		//System.out.println(StatDateAndTimeUtil.getDateStrByMillitime(timelong));
    		//String rowkey=getRowKey(13355,StatDateAndTimeUtil.getCurrentDate().getTime());
//    		String rowkey="a5e9eeab9a92ab47c09a40ee4e5b299e_9223370531559306631";
//    		String htableName="car_gps_log_201709";
//    		rangeScan(htableName,rowkey,rowkey,true);

    		 
    		//System.out.println(rowkey);
    		//tablename
    		//rowKey
    		//familyName
    		//columnName
    		//value
    		/*String rowkey=getRowKey(80779,StatDateAndTimeUtil.getCurrentDate().getTime());
    		
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"driverId","80779");
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"accuracy","4");
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"appType","1");
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"carType","34");
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"endPositionTime","1505288585");
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"inCoordType","mars");
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"lastPositionTime","1505288302");
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"latitude","25.985078125");
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"longitude","119.38930338541667");
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"networkType","1");
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"orderId","P35505284805664855");
    		
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"orderStatus","3");
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"positionTime","1505288301");
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"provider","gps");
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"serviceType","2");
    		
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"startPositionTime","1505288302");
    		updateColumnValue(htableName,rowkey,DEFAULT_COLUMN_FAMILY,"type","3");
    		*/
    		//System.out.println(rowkey);

            Properties bdConfProperties = PropertiesUtils.getProperties("jdbc.properties");
            String ip = bdConfProperties.getProperty("hadoop.namenode.ip");
            System.out.println(ip);
        }catch(Exception e){
    		e.printStackTrace();
    	}
    	
    }


}