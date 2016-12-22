This code reproduces what I think is a bug of Log4j2.
It's a simple loop that logs 2000 messages with two appenders:
a console appender and a rolling file appender that rolls the file
every 5Kb.

What is strange is that when the application is launched with
`mvn exec:java` you will see this log in the console.

    2016-12-22 22:12:36 INFO This is log message #1993.
    2016-12-22 22:12:36 INFO This is log message #1994.
    2016-12-22 22:12:36 INFO This is log message #1995.
    2016-12-22 22:12:36 INFO This is log message #1996.
    2016-12-22 22:12:36 INFO This is log message #1997.
    2016-12-22 22:12:36 INFO This is log message #1998.
    2016-12-22 22:12:36 INFO This is log message #1999.
    2016-12-22 22:13:36,380 pool-1-thread-1 DEBUG Stopping LoggerContext[name=60199c81, org.apache.logging.log4j.core.LoggerContext@4597ec68]
    2016-12-22 22:13:36,380 pool-1-thread-1 DEBUG Stopping LoggerContext[name=60199c81, org.apache.logging.log4j.core.LoggerContext@4597ec68]...
    2016-12-22 22:13:36,381 pool-1-thread-1 TRACE Unregistering 1 MBeans: [org.apache.logging.log4j2:type=60199c81]
    2016-12-22 22:13:36,381 pool-1-thread-1 TRACE Unregistering 1 MBeans: [org.apache.logging.log4j2:type=60199c81,component=StatusLogger]
    2016-12-22 22:13:36,381 pool-1-thread-1 TRACE Unregistering 1 MBeans: [org.apache.logging.log4j2:type=60199c81,component=ContextSelector]
    2016-12-22 22:13:36,381 pool-1-thread-1 TRACE Unregistering 2 MBeans: [org.apache.logging.log4j2:type=60199c81,component=Loggers,name=bug, org.apache.logging.log4j2:type=60199c81,component=Lo
    ggers,name=]
    2016-12-22 22:13:36,381 pool-1-thread-1 TRACE Unregistering 2 MBeans: [org.apache.logging.log4j2:type=60199c81,component=Appenders,name=roll-by-size, org.apache.logging.log4j2:type=60199c81,c
    omponent=Appenders,name=stdout]
    2016-12-22 22:13:36,382 pool-1-thread-1 TRACE Unregistering but no MBeans found matching 'org.apache.logging.log4j2:type=60199c81,component=AsyncAppenders,name=*'
    2016-12-22 22:13:36,382 pool-1-thread-1 TRACE Unregistering but no MBeans found matching 'org.apache.logging.log4j2:type=60199c81,component=AsyncLoggerRingBuffer'
    2016-12-22 22:13:36,382 pool-1-thread-1 TRACE Unregistering but no MBeans found matching 'org.apache.logging.log4j2:type=60199c81,component=Loggers,name=*,subtype=RingBuffer'
    2016-12-22 22:13:36,382 pool-1-thread-1 TRACE Stopping XmlConfiguration[location=C:\Users\danidemi\workspace\bug-log4j2-hanging-up-before-shutdown\target\classes\log4j2.xml]...
    2016-12-22 22:13:36,382 pool-1-thread-1 TRACE XmlConfiguration notified 3 ReliabilityStrategies that config will be stopped.
    2016-12-22 22:13:36,382 pool-1-thread-1 TRACE XmlConfiguration stopping 2 LoggerConfigs.
    2016-12-22 22:13:36,382 pool-1-thread-1 TRACE XmlConfiguration stopping root LoggerConfig.
    2016-12-22 22:13:36,382 pool-1-thread-1 TRACE XmlConfiguration notifying ReliabilityStrategies that appenders will be stopped.
    2016-12-22 22:13:36,382 pool-1-thread-1 TRACE XmlConfiguration stopping remaining Appenders.
    2016-12-22 22:13:36,383 pool-1-thread-1 DEBUG Shutting down RollingFileManager target/log4j2/roll-by-size/app.log
    2016-12-22 22:13:36,383 pool-1-thread-1 DEBUG Shut down RollingFileManager target/log4j2/roll-by-size/app.log, all resources released: true
    2016-12-22 22:13:36,383 pool-1-thread-1 DEBUG Shutting down OutputStreamManager SYSTEM_OUT.false.false
    2016-12-22 22:13:36,383 pool-1-thread-1 DEBUG Shut down OutputStreamManager SYSTEM_OUT.false.false, all resources released: true
    2016-12-22 22:13:36,384 pool-1-thread-1 TRACE XmlConfiguration stopped 2 remaining Appenders.
    2016-12-22 22:13:36,384 pool-1-thread-1 TRACE XmlConfiguration cleaning Appenders from 3 LoggerConfigs.
    2016-12-22 22:13:36,384 pool-1-thread-1 DEBUG Stopped XmlConfiguration[location=C:\Users\danidemi\workspace\bug-log4j2-hanging-up-before-shutdown\target\classes\log4j2.xml] OK
    2016-12-22 22:13:36,385 pool-1-thread-1 DEBUG Stopped LoggerContext[name=60199c81, org.apache.logging.log4j.core.LoggerContext@4597ec68]...

What is strange is that the last log is issued at a certain time

    2016-12-22 22:12:36 INFO This is log message #1999.

but the shutdown of og4j2 starts __exactly__ one minute after the last log message.

    2016-12-22 22:13:36,380 pool-1-thread-1 DEBUG Stopping LoggerContext[name=60199c81, org.apache.logging.log4j.core.LoggerContext@4597ec68]

This difference in time seems more or less independent from the number of messages being logged.

However, if you change the `log4j2.xml` incrementing the size from 5Kb...

    <Policies>
        <OnStartupTriggeringPolicy/>
        <SizeBasedTriggeringPolicy size="5 KB"/>
    </Policies>

to 5Mb...

    <Policies>
        <OnStartupTriggeringPolicy/>
        <SizeBasedTriggeringPolicy size="5 MB"/>
    </Policies>

... that makes the application to stop immediately right after the last log message.

