package ytc.wp.costomdep

import groovy.util.logging.Slf4j

/**
 * Created by wangpeng on 17-9-23.
 */
@Slf4j
class Lg {

    def static error(mags){
        log.error(mags)
    }

    def static dubug(mags){
        log.debug(mags)
    }

    def static info(mags){
        log.info(mags)
    }

    def static warn(mags){
        log.warn(mags)
    }

    def static trace(mags){
        log.trace(mags)
    }
}
