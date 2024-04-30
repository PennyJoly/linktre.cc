/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jsnjfz.manage;

import cn.stylefeng.roses.core.config.WebAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SpringBoot方式启动类
 *
 * @author kevin不会写代码
 * @Date 2024/04/30 12:06
 */
@SpringBootApplication(exclude = WebAutoConfiguration.class)
public class SiteApplication {

    private final static Logger logger = LoggerFactory.getLogger(SiteApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SiteApplication.class, args);
        logger.info("\n" +
                " .----..-. .---. .----.\n" +
                "{ {__  | |{_   _}| {_  \n" +
                ".-._} }| |  | |  | {__ \n" +
                "`----' `-'  `-'  `----'\n启动成功,获取更多开源项目信息请邮件联系c2500338766@icloud.com\n或访问twitter：https://twitter.com/pennyjoly/status/1783305526119662061?s=46\n访问github:https://github.com/PennyJoly/linktre.cc!");
    }
}
