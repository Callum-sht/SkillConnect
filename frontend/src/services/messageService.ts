export type Message = {
  messageId: number
  conversationId: number
  senderId: number
  content: string
  sentAt: string
}

export async function getMessages(
  conversationId: number,
): Promise<Message[]> {
  const response = await fetch(
    `/api/conversations/${conversationId}/messages`,
    {
      headers: {
        Accept: 'application/json',
      },
    },
  )

  if (!response.ok) {
    const message = await response.text()

    throw new Error(
      message || 'Unable to load messages.',
    )
  }

  return await response.json()
}

export async function sendMessage(
  conversationId: number,
  content: string,
): Promise<Message> {
  const response = await fetch(
    `/api/conversations/${conversationId}/messages`,
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: JSON.stringify({ content }),
    },
  )

  if (!response.ok) {
    const message = await response.text()

    throw new Error(
      message || 'Unable to send message.',
    )
  }

  return await response.json()
}

export async function markMessagesAsRead(
  conversationId: number,
): Promise<void> {
  const response = await fetch(
    `/api/conversations/${conversationId}/messages/read`,
    {
      method: 'PUT',
    },
  )

  if (!response.ok) {
    const message = await response.text()

    throw new Error(
      message || 'Unable to mark messages as read.',
    )
  }
}