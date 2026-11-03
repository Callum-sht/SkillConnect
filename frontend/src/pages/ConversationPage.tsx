import { useEffect, useState } from 'react'
import {
  getConversation,
  type Conversation,
} from '../services/conversationService'
import {
  getMessages,
  markMessagesAsRead,
  sendMessage,
  type Message,
} from '../services/messageService'
import { getCurrentUserId } from '../services/authService'

function getConversationIdFromPath(): number | null {
  const match =
    window.location.pathname.match(/^\/messages\/(\d+)$/)

  return match ? Number(match[1]) : null
}

function formatMessageTime(sentAt: string): string {
  const date = new Date(sentAt)

  if (Number.isNaN(date.getTime())) {
    return ''
  }

  return date.toLocaleString([], {
    dateStyle: 'medium',
    timeStyle: 'short',
  })
}

function ConversationPage() {
  const conversationId = getConversationIdFromPath()
  const currentUserId = getCurrentUserId()

  const [conversation, setConversation] =
    useState<Conversation | null>(null)

  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [messages, setMessages] = useState<Message[]>([])
  const [messagesError, setMessagesError] =
    useState<string | null>(null)

  const [messageInput, setMessageInput] = useState('')
  const [isSending, setIsSending] = useState(false)
  const [sendError, setSendError] =
    useState<string | null>(null)

  useEffect(() => {
    if (conversationId === null) {
      setError('Invalid conversation.')
      setIsLoading(false)
      return
    }

    getConversation(conversationId)
      .then((result) => {
        setConversation(result)
      })
      .catch((requestError) => {
        setError(
          requestError instanceof Error
            ? requestError.message
            : 'Unable to load conversation.',
        )
      })
      .finally(() => {
        setIsLoading(false)
      })
  }, [conversationId])

  useEffect(() => {
    if (conversationId === null) {
      return
    }

    let isActive = true

    const loadMessages = async () => {
      try {
        const result = await getMessages(conversationId)

        if (isActive) {
          setMessages(result)
          setMessagesError(null)

          await markMessagesAsRead(
                conversationId,
            )
        }
      } catch (requestError) {
        if (isActive) {
          setMessagesError(
            requestError instanceof Error
              ? requestError.message
              : 'Unable to load messages.',
          )
        }
      }
    }

    loadMessages()

    const intervalId = window.setInterval(
      loadMessages,
      2000,
    )

    return () => {
      isActive = false
      window.clearInterval(intervalId)
    }
  }, [conversationId])

  const handleSendMessage = async (
    event: React.FormEvent<HTMLFormElement>,
  ) => {
    event.preventDefault()

    if (conversationId === null) {
      return
    }

    const content = messageInput.trim()

    if (!content) {
      setSendError('Message cannot be empty.')
      return
    }

    setIsSending(true)
    setSendError(null)

    try {
      const newMessage = await sendMessage(
        conversationId,
        content,
      )

      setMessages((currentMessages) => {
        const alreadyExists = currentMessages.some(
          (message) =>
            message.messageId === newMessage.messageId,
        )

        if (alreadyExists) {
          return currentMessages
        }

        return [...currentMessages, newMessage]
      })

      setMessageInput('')
    } catch (requestError) {
      setSendError(
        requestError instanceof Error
          ? requestError.message
          : 'Unable to send message.',
      )
    } finally {
      setIsSending(false)
    }
  }

  if (isLoading) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-12">
        <p className="text-muted">
          Loading conversation...
        </p>
      </div>
    )
  }

  if (currentUserId === null) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-12">
        <p
          className="rounded-xl border border-red-200 bg-red-50 p-4 text-red-700"
          role="alert"
        >
          Please sign in to view this conversation.
        </p>
      </div>
    )
  }

  if (error || conversation === null) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-12">
        <h1 className="text-2xl font-bold">
          Conversation unavailable
        </h1>

        <p className="mt-2 text-muted">
          {error ?? 'Unable to load conversation.'}
        </p>
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-3xl px-4 py-12">
      <button
        type="button"
        className="text-sm font-semibold text-brand hover:underline"
        onClick={() => window.history.back()}
      >
        Back
      </button>

      <div className="mt-6 overflow-hidden rounded-2xl border border-line bg-white">
        <div className="border-b border-line p-5">
          <p className="text-sm text-muted">
            Conversation with
          </p>

          <h1 className="mt-1 text-2xl font-bold">
            {conversation.otherParticipantName}
          </h1>
        </div>

        <div className="p-5">
          {messagesError && (
            <p
              className="mb-4 text-sm text-red-600"
              role="alert"
            >
              {messagesError}
            </p>
          )}

          {messages.length === 0 ? (
            <div className="py-8 text-center">
              <p className="text-muted">
                No messages yet.
              </p>

              <p className="mt-1 text-sm text-muted">
                Start the conversation by sending a message.
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              {messages.map((message) => {
                const isOwnMessage =
                  message.senderId === currentUserId

                return (
                  <div
                    key={message.messageId}
                    className={`flex ${
                      isOwnMessage
                        ? 'justify-end'
                        : 'justify-start'
                    }`}
                  >
                    <div
                      className={`max-w-[75%] rounded-xl border p-3 ${
                        isOwnMessage
                          ? 'border-violet-200 bg-violet-50'
                          : 'border-line bg-white'
                      }`}
                    >
                      <p className="break-words text-sm">
                        {message.content}
                      </p>

                      <p className="mt-1 text-xs text-muted">
                        {isOwnMessage
                            ? 'You'
                            : conversation.otherParticipantName}
                        {' · '}
                        {formatMessageTime(message.sentAt)}
                        </p>
                    </div>
                  </div>
                )
              })}
            </div>
          )}
        </div>

        <form
          onSubmit={handleSendMessage}
          className="border-t border-line p-4"
        >
          {sendError && (
            <p
              className="mb-2 text-sm text-red-600"
              role="alert"
            >
              {sendError}
            </p>
          )}

          <div className="flex items-end gap-3">
            <textarea
              value={messageInput}
              onChange={(event) => {
                setMessageInput(event.target.value)

                if (sendError) {
                  setSendError(null)
                }
              }}
              placeholder="Type a message..."
              rows={2}
              maxLength={2000}
              disabled={isSending}
              className="min-h-[52px] flex-1 resize-none rounded-xl border border-line px-4 py-3 text-sm outline-none focus:border-violet-400 focus:ring-2 focus:ring-violet-100 disabled:opacity-60"
              aria-label="Message"
            />

            <button
              type="submit"
              disabled={
                isSending ||
                messageInput.trim().length === 0
              }
              className="rounded-xl bg-violet-600 px-5 py-3 text-sm font-medium text-white transition hover:bg-violet-700 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {isSending ? 'Sending...' : 'Send'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default ConversationPage