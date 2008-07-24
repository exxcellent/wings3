@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapters( { @XmlJavaTypeAdapter(value = UrlAdapter.class, type = URL.class),
		@XmlJavaTypeAdapter(value = ClassAdapter.class, type = Class.class) })
package org.wings.conf;

import java.net.URL;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

