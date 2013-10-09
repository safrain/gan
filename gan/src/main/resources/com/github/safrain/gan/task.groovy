_gan_running = {
    _this.runningInfos.each { com.github.safrain.gan.GANFilter.RunInfo r ->
        println ""
        println "         UUID: ${r.uuid}"
        println "    StartTime: ${r.startTime}"
        println "       Thread: ${r.thread}" + (Thread.currentThread() == r.thread ? "(Current thread)" : "")
        println "RemoteAddress: ${r.remoteAddress}"
        println "ScriptContent:\n\n${r.scriptContent}\n"
        80.times { print "=" }
        println ""
    }
}

_gan_kill = { uuid ->
    _this.runningInfos.find { it.uuid == uuid }.thread.interrupt()
}