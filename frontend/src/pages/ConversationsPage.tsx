import { useEffect, useState } from 'react'
import {
  getConversations,
  type ConversationSummary,
} from '../services/conversationService'

export default function ConversationsPage() {
  const [conversations, setConversations] =
    useState<ConversationSummary[]>([])

  const [isLoading, setIsLoading] =
    useState(true)

  const [error, setError] =
    useState<string | null>(null)

  useEffect(() => {
    let isActive = true

    async function loadConversations() {
        try {
        const result =
            await getConversations()

        if (isActive) {
            setConversations(result)
            setError(null)
        }
        } catch (err) {
        if (isActive) {
            setError(
            err instanceof Error
                ? err.message
                : 'Unable to load conversations.',
            )
        }
        } finally {
        if (isActive) {
            setIsLoading(false)
        }
        }
    }

    loadConversations()

    const intervalId = window.setInterval(
        loadConversations,
        2000,
    )

    return () => {
        isActive = false
        window.clearInterval(intervalId)
    }
    }, [])

  function openConversation(
    conversationId: number,
  ) {
    window.location.href =
      `/messages/${conversationId}`
  }

  function formatTime(value: string | null) {
    if (!value) {
      return ''
    }

    const date = new Date(value)

    return date.toLocaleString([], {
      dateStyle: 'short',
      timeStyle: 'short',
    })
  }

  if (isLoading) {
    return (
      <main className="mx-auto max-w-4xl p-6">
        <p>Loading conversations...</p>
      </main>
    )
  }

  if (error) {
    return (
      <main className="mx-auto max-w-4xl p-6">
        <h1 className="text-2xl font-semibold">
          Messages
        </h1>

        <p className="mt-4 text-red-600">
          {error}
        </p>
      </main>
    )
  }

  return (
    <main className="mx-auto max-w-4xl p-6">
      <h1 className="mb-6 text-2xl font-semibold">
        Messages
      </h1>

      {conversations.length === 0 ? (
        <div className="rounded-lg border p-6">
          <p>You do not have any conversations yet.</p>
        </div>
      ) : (
        <div className="overflow-hidden rounded-lg border">
          {conversations.map((conversation) => (
            <button
              key={conversation.conversationId}
              type="button"
              onClick={() =>
                openConversation(
                  conversation.conversationId,
                )
              }
              className="flex w-full items-center justify-between gap-4 border-b p-4 text-left last:border-b-0 hover:bg-gray-50"
            >
              <div className="min-w-0">
                <p className="font-semibold">
                  {conversation.otherParticipantName}
                </p>

                <p className="truncate text-sm text-gray-600">
                  {conversation.latestMessage ??
                    'No messages yet'}
                </p>
              </div>

              <div className="flex shrink-0 flex-col items-end gap-2">
                <time className="text-sm text-gray-500">
                    {formatTime(
                    conversation.latestMessageTime,
                    )}
                </time>

                {conversation.unreadCount > 0 && (
                    <span
                    className="inline-flex min-w-6 items-center justify-center rounded-full bg-violet-600 px-2 py-0.5 text-xs font-semibold text-white"
                    aria-label={`${conversation.unreadCount} unread messages`}
                    >
                    {conversation.unreadCount}
                    </span>
                )}
                </div>
            </button>
          ))}
        </div>
      )}
    </main>
  )
}