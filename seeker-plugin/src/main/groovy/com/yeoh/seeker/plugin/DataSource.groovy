package com.yeoh.seeker.plugin

/**
 * Used to save seeker config
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-09-27
 */
class DataSource {

    def static seekerConfig = [:]

    def static clear() {
        seekerConfig = [:]
    }
}