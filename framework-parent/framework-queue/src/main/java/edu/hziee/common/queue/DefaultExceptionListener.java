package edu.hziee.common.queue;

import java.util.List;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: DefaultExceptionListener.java 89 2011-05-05 02:24:52Z
 *          archie.wang $
 */
public class DefaultExceptionListener implements ExceptionListener<String> {

  private FileWriteExecutor fileWriteExecutor;

  @Override
  public void onException(List<String> strings) {
    fileWriteExecutor.execute(strings);
  }

  public void setBackupDir(String backupDir) {
    fileWriteExecutor.setFileDir(backupDir);
  }

  public void setFileName(String fileName) {
    fileWriteExecutor.setFileName(fileName);
  }

  public void setFileWriteExecutor(FileWriteExecutor fileWriteExecutor) {
    this.fileWriteExecutor = fileWriteExecutor;
  }

}
