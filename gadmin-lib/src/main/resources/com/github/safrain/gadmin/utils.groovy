import com.github.safrain.gadmin.GAdminFilter

gadmin = new Object()
gadmin.metaClass.propertyMissing = {
    ([
            running: {
                _this.runningInfos.each { GAdminFilter.RunInfo r ->
                    println ""
                    println "         UUID: ${r.uuid}"
                    println "    StartTime: ${r.startTime}"
                    println "       Thread: ${r.thread}"
                    println "RemoteAddress: ${r.remoteAddress}"
                    println "ScriptContent:\n${r.scriptContent}\n"
                    80.times { print "=" }
                    println ""
                }
            }
    ])[it]();
}
gadmin.metaClass.kill = { uuid ->
    _this.runningInfos.find { it.uuid == uuid }.thread.interrupt()
}