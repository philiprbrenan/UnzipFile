//------------------------------------------------------------------------------
// Unzip a file
// Philip R Brenan at gmail dot com, Appa Apps Ltd, 2017
//------------------------------------------------------------------------------
package com.appaapps.unzip;

import java.io.*;
import java.util.*;
import java.util.zip.*;

abstract public class Unzip extends Thread                                      // A thread to unzip a file
 {public final String file;                                                     // File being unzipped
  public Exception exception = null;                                            // Exception that occurred during the unzip
  private ZipFile createZipFile(File file)
   {try
     {return new ZipFile(file);
     }
    catch(Exception e) {exception = e; failed(this);}
    return null;
   }

  public Unzip(String File)                                                     // Unzip the named file
   {file = File;
   }

  public void run()                                                             // Unzip the named file
   {final ZipFile z = createZipFile(new File(file));
    for(final Enumeration<? extends ZipEntry> e = z.entries();                  // Each zip file entry
        e.hasMoreElements();)
     {final ZipEntry   ze = (ZipEntry)e.nextElement();
      if (!ze.isDirectory())
       {try
         {final InputStream i = z.getInputStream(ze);
          final int size = (int)ze.getSize();                                   // Assume less than 2GB
          final byte[]b = new byte[size];
          int offset = 0;
          for(;offset < b.length;)
           {final int c = i.read(b, offset, b.length-offset);
            if (c == -1) break;
            offset += c;
           }
          zipEntry(this, ze.getName(), b);                                      // Report a zip entry as read
         }
        catch(Exception x) {exception = x; failed(this);}
       }
     }
    finished(this);                                                             // Report the unzip as finished
   }
// Override these methods to observe progress
  public void failed  (Unzip z) {}                                              // Report a failure during the unzip
  public void finished(Unzip z) {}                                              // Report the unzip as finished
  abstract public void zipEntry(final Unzip z, final String name,                          // Override to process the content of each zip entry
                                final byte[]content);

  public static void main(String[] args)                                        // Unzip a file
   {new Unzip("/home/phil/z/java/unzip/Unzip.java.zip")
     {public void zipEntry(Unzip z, String name, byte[]content)
       {say(name);
        say(new String(content), " ", content.length);
       }
      public void finished(final Unzip z)
       {say("Finished!");
       }
     }.start();
   }

  static void say(Object...O)                                                   // Say something
   {final StringBuilder b = new StringBuilder();
    for(Object o: O) b.append(o.toString());
    System.err.print(b.toString()+"\n");
   }
 }
