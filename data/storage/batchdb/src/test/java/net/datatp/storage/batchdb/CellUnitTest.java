package net.datatp.storage.batchdb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.datatp.storage.batchdb.Bytes;
import net.datatp.storage.batchdb.Cell;

import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Test;

public class CellUnitTest {

  @Test
  public void test() throws Exception {
    Cell cell = new Cell() ;

    byte _byte = 0x00 ;
    cell.addField("byte", _byte) ;

    boolean _boolean = true ;
    cell.addField("boolean", _boolean) ;

    byte[] bytes = new byte[] { 0x1 , 0x2 } ;
    cell.addField("bytes", bytes) ;

    double _double = 12000000000000.56D ;
    cell.addField("double", _double) ;

    float _float = 0.345678F ;
    cell.addField("float", _float) ;

    int _int = 12 ;
    cell.addField("int", _int) ;

    long _long = System.currentTimeMillis() ;
    cell.addField("long", _long) ;

    short _short = 1 ;
    cell.addField("short", _short) ;

    String _string = "String" ;
    cell.addField("string", _string) ;

    Text _text = new Text("Text") ;
    cell.addField("text", _text) ;

    cell.addField(new Text("key"), new Text("value")) ;

    Assert.assertEquals(11, cell.size()) ;
    Assert.assertEquals(0, Bytes.compareTo(cell.getFieldAsBytes("bytes"), 0, bytes.length, bytes, 0, bytes.length)) ;
    Assert.assertEquals(cell.getFieldAsText(new Text("key")), new Text("value")) ;
    Assert.assertEquals(cell.getFieldAsBoolean("boolean"), _boolean) ;
    Assert.assertEquals(cell.getFieldAsByte("byte"), _byte) ;
    Assert.assertEquals(cell.getFieldAsDouble("double"), _double, 0.000000001) ;
    Assert.assertEquals(cell.getFieldAsFloat("float"), _float, 0.000000001) ;
    Assert.assertEquals(cell.getFieldAsInt("int"), _int) ;
    Assert.assertEquals(cell.getFieldAsLong("long"), _long) ;
    Assert.assertEquals(cell.getFieldAsShort("short"), _short) ;
    Assert.assertEquals(cell.getFieldAsString("string"), _string) ;
    Assert.assertEquals(cell.getFieldAsText("text"), _text) ;

    ByteArrayOutputStream os = new ByteArrayOutputStream() ;
    ObjectOutputStream out = new ObjectOutputStream(os) ;
    cell.write(out) ;
    out.close() ;

    Cell sel = new Cell() ;
    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(os.toByteArray())) ;
    sel.readFields(in) ;

    Assert.assertEquals(11, sel.size()) ;
    Assert.assertEquals(Bytes.compareTo(sel.getFieldAsBytes("bytes"), 0, bytes.length, bytes, 0, bytes.length), 0) ;
    Assert.assertEquals(sel.getFieldAsText(new Text("key")), new Text("value")) ;
    Assert.assertEquals(sel.getFieldAsBoolean("boolean"), _boolean) ;
    Assert.assertEquals(sel.getFieldAsByte("byte"), _byte) ;
    Assert.assertEquals(sel.getFieldAsDouble("double"), _double, 0.000000001) ;
    Assert.assertEquals(sel.getFieldAsFloat("float"), _float, 0.000000001) ;
    Assert.assertEquals(sel.getFieldAsInt("int"), _int) ;
    Assert.assertEquals(sel.getFieldAsLong("long"), _long) ;
    Assert.assertEquals(sel.getFieldAsShort("short"), _short) ;
    Assert.assertEquals(sel.getFieldAsString("string"), _string) ;
    Assert.assertEquals(sel.getFieldAsText("text"), _text) ;

    sel.removeField("text") ;
    sel.removeField(new Text("key")) ;
    Assert.assertEquals(9, sel.size()) ;
  }
}