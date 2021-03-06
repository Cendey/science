package cn.com.nettex.apps.intf;

/**
 * <p>Title: science</p>
 * <p>Description: Factory</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/20/2018
 */
public interface Factory {

    Reader createReader();

    Writer createWriter();
}
