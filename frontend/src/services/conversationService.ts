export type Conversation = {
  conversationId: number
  seekerId: number
  providerId: number
  otherParticipantName: string
  createdAt: string | null
  updatedAt: string | null
}

export type ConversationSummary = {
  conversationId: number
  otherParticipantName: string
  latestMessage: string | null
  latestMessageTime: string | null
  unreadCount: number
}

export async function startConversation(
  providerId: number,
): Promise<Conversation> {
  const response = await fetch('/api/conversations', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Accept: 'application/json',
    },
    body: JSON.stringify({ providerId }),
  })

  if (!response.ok) {
    const message = await response.text()

    throw new Error(
      message || 'Unable to start conversation.',
    )
  }

  return await response.json()
}

export async function getConversation(
  conversationId: number,
): Promise<Conversation> {
  const response = await fetch(
    `/api/conversations/${conversationId}`,
    {
      headers: {
        Accept: 'application/json',
      },
    },
  )

  if (!response.ok) {
    const message = await response.text()

    throw new Error(
      message || 'Unable to load conversation.',
    )
  }

  return await response.json()
}

export async function getConversations(): Promise<ConversationSummary[]> {
  const response = await fetch('/api/conversations', {
    method: 'GET',
    headers: {
      Accept: 'application/json',
    },
  })

  if (!response.ok) {
    const message = await response.text()
    throw new Error(
      message || 'Unable to load conversations.',
    )
  }

  return await response.json()
}