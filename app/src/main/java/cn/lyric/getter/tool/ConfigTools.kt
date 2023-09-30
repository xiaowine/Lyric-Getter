/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * https://github.com/577fkj/StatusBarLyric
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by 577fkj.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/577fkj/StatusBarLyric/blob/main/LICENSE>.
 */

package cn.lyric.getter.tool

import android.annotation.SuppressLint
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.config.Config

@SuppressLint("StaticFieldLeak")
object ConfigTools {
    val config: Config by lazy { Config(ActivityTools.application, BuildConfig.APPLICATION_ID, false) }
    val xConfig: Config by lazy { Config(null, BuildConfig.APPLICATION_ID, true) }

    fun updateConfigVer() {

    }
}
