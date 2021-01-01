/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
package org.piangles.core.resources;

import java.util.ArrayList;
import java.util.List;

public final class ConsumerProperties
{
	private String groupId;
	private List<Topic> topics = null;
	
	public ConsumerProperties(String groupId)
	{
		this.groupId = groupId;
		this.topics = new ArrayList<>();
	}
	
	public String getGroupId()
	{
		return groupId;
	}
	
	public List<Topic> getTopics()
	{
		return topics;
	}

	public void setTopics(List<Topic> topics)
	{
		this.topics = topics;
	}

	public class Topic
	{
		public String topicName;
		public int partitionNo;
		public int offset;
		public boolean compacted;

		public Topic(String topicName, int partitionNo, boolean compacted)
		{
			this(topicName, partitionNo, compacted, 0);
		}

		public Topic(String topicName, int partitionNo, boolean compacted, int offset)
		{
			this.topicName = topicName;
			this.partitionNo = partitionNo;
			this.compacted = compacted;
			this.offset = offset;
		}

		@Override
		public String toString()
		{
			return "Topic [topicName=" + topicName + ", partitionNo=" + partitionNo + ", offset=" + offset + ", compacted=" + compacted + "]";
		}
	}

	@Override
	public String toString()
	{
		return "ConsumerProperties [groupId=" + groupId + ", topics=" + topics + "]";
	}
}
