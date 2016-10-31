/*
 * semanticcms-news-taglib - SemanticCMS newsfeeds in a JSP environment.
 * Copyright (C) 2016  AO Industries, Inc.
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
package com.semanticcms.news.taglib;

import com.aoindustries.io.TempFileList;
import com.aoindustries.io.buffer.AutoTempFileWriter;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.io.buffer.BufferWriter;
import com.aoindustries.servlet.filter.TempFileContext;
import static com.aoindustries.taglib.AttributeUtils.resolveValue;
import com.aoindustries.taglib.AutoEncodingBufferedTag;
import static com.aoindustries.util.StringUtility.nullIfEmpty;
import com.semanticcms.core.model.ElementContext;
import com.semanticcms.core.servlet.CaptureLevel;
import com.semanticcms.core.servlet.PageUtils;
import com.semanticcms.core.servlet.SemanticCMS;
import com.semanticcms.core.taglib.ElementTag;
import com.semanticcms.news.model.News;
import com.semanticcms.news.servlet.impl.NewsImpl;
import java.io.IOException;
import java.io.Writer;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

public class NewsTag extends ElementTag<News> {

	private ValueExpression book;
	public void setBook(ValueExpression book) {
		this.book = book;
	}

	private ValueExpression page;
	public void setPage(ValueExpression page) {
		this.page = page;
	}

	private ValueExpression element;
	public void setElement(ValueExpression element) {
		this.element = element;
	}

	private ValueExpression view;
	public void setView(ValueExpression view) {
		this.view = view;
	}

	private ValueExpression title;
	public void setTitle(ValueExpression title) {
		this.title = title;
	}

	private ValueExpression description;
	public void setDescription(ValueExpression description) {
		this.description = description;
	}

	private ValueExpression pubDate;
	public void setPubDate(ValueExpression pubDate) {
		this.pubDate = pubDate;
	}

	@Override
	protected News createElement() {
		return new News();
	}

	@Override
	protected void evaluateAttributes(News news, ELContext elContext) throws JspTagException, IOException {
		super.evaluateAttributes(news, elContext);
		news.setBook(resolveValue(book, String.class, elContext));
		news.setTargetPage(resolveValue(page, String.class, elContext));
		news.setElement(resolveValue(element, String.class, elContext));
		String viewStr = nullIfEmpty(resolveValue(view, String.class, elContext));
		if(viewStr == null) viewStr = SemanticCMS.DEFAULT_VIEW_NAME;
		news.setView(viewStr);
		news.setTitle(resolveValue(title, String.class, elContext));
		news.setDescription(resolveValue(description, String.class, elContext));
		news.setPubDate(PageUtils.toDateTime(resolveValue(pubDate, Object.class, elContext)));
	}

	private BufferResult writeMe;
	@Override
	protected void doBody(News news, CaptureLevel captureLevel) throws JspException, IOException {
		try {
			super.doBody(news, captureLevel);
			final PageContext pageContext = (PageContext)getJspContext();
			final HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			BufferWriter capturedOut;
			if(captureLevel == CaptureLevel.BODY) {
				// Enable temp files if temp file context active
				capturedOut = TempFileContext.wrapTempFileList(
					AutoEncodingBufferedTag.newBufferWriter(),
					request,
					// Java 1.8: AutoTempFileWriter::new
					new TempFileContext.Wrapper<BufferWriter>() {
						@Override
						public BufferWriter call(BufferWriter original, TempFileList tempFileList) {
							return new AutoTempFileWriter(original, tempFileList);
						}
					}
				);
			} else {
				capturedOut = null;
			}
			try {
				NewsImpl.writeNewsImpl(
					pageContext.getServletContext(),
					request,
					(HttpServletResponse)pageContext.getResponse(),
					capturedOut,
					news
				);
			} finally {
				if(capturedOut != null) capturedOut.close();
			}
			writeMe = capturedOut==null ? null : capturedOut.getResult();
		} catch(ServletException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public void writeTo(Writer out, ElementContext context) throws IOException {
		if(writeMe != null) writeMe.writeTo(out);
	}
}
