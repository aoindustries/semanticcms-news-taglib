/*
 * semanticcms-news-taglib - SemanticCMS newsfeeds in a JSP environment.
 * Copyright (C) 2016, 2017, 2019, 2020  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of semanticcms-news-taglib.
 *
 * semanticcms-news-taglib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * semanticcms-news-taglib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with semanticcms-news-taglib.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.semanticcms.news.taglib.book;

import com.semanticcms.tagreference.TagReferenceInitializer;
import java.util.Collections;

public class SemanticCmsNewsTldInitializer extends TagReferenceInitializer {

	@SuppressWarnings("unchecked")
	public SemanticCmsNewsTldInitializer() {
		super(
			Maven.properties.getProperty("project.name") + " Reference",
			"Taglib Reference",
			"/news/taglib",
			"/semanticcms-news.tld",
			true,
			Maven.properties.getProperty("documented.javadoc.link.javase"),
			Maven.properties.getProperty("documented.javadoc.link.javaee"),
			// Self
			Collections.singletonMap("com.semanticcms.news.taglib", Maven.properties.getProperty("project.url") + "apidocs/"),
			// Dependencies
			Collections.singletonMap("com.semanticcms.core.model", "https://semanticcms.com/core/model/apidocs/"),
			Collections.singletonMap("com.semanticcms.news.model", "https://semanticcms.com/news/model/apidocs/")
		);
	}
}
