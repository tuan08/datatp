package net.datatp.vm.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import net.datatp.util.dataformat.DataSerializer;

public class CommandResult<T> {
  private T result ;
  private boolean discardResult ;
  private String errorStacktrace;
  
  public <V> V getResultAs(Class<V> type) { return (V) result; }
  
  @JsonDeserialize(using=DataSerializer.GenericTypeDeserializer.class)
  public T getResult() { return result; }
  
  @JsonSerialize(using=DataSerializer.GenericTypeSerializer.class)
  public void setResult(T result) { this.result = result; }

  public boolean isDiscardResult() { return discardResult; }
  public void setDiscardResult(boolean discardResult) {
    this.discardResult = discardResult;
  }

  public String getErrorStacktrace() { return errorStacktrace; }
  public void setErrorStacktrace(String errorStacktrace) { this.errorStacktrace = errorStacktrace; }
}
