# Sprint Retro Notes

**Date of the Meeting:** 9/10/2026 
**Team:** G07-04 
**Sprint #:** Sprint2

## Attendance

**Attended:**
- Scrum Master: Hantao Shen
- Development Team: Henry, Ray, Zitong, Lia

**Absent:**
- 

---

## 1. What Went Well?

- Compared with Sprint 1, the team had a better understanding of the project requirements and what needed to be completed.
- Team members worked faster because everyone was more familiar with the Spring Boot code structure.
- The team discussed several possible enhancement features before making a final decision.
- When considering email notifications, the team found that SkillConnect does not check whether users enter valid email addresses during registration.
- Because email notifications might be sent to invalid addresses, the team decided to develop in-app messaging instead.
- Team communication and decision-making were better than in Sprint 1.

## 2. What Went Not So Well?

- The team was still not very familiar with the GitHub Project board.
- Some team members did not move completed issues to **Done** on time.
- Because of this, the burndown chart did not show the team’s progress correctly.
- The team spent too much time discussing which enhancement feature to choose.
- In-app messaging was selected and started later than planned.
- This created some time pressure near the end of the sprint.

## 3. Lessons Learned

### What should be improved or done differently in the team's process?

- Team members should update their GitHub issues when they start or finish a task.
- The team should check the GitHub Project board during every Scrum meeting.
- A deadline should be set for choosing major features.
- When comparing features, the team should consider development time, difficulty, risks, and project requirements.
- Large features should be divided into smaller user stories and assigned earlier.
- Important technical decisions should continue to be recorded in ADRs.

### Was there anything unexpected that should be taken into account in future sprints?

- The team first planned to develop email notifications.
- However, the application does not verify users’ email addresses during registration.
- This means notifications could be sent to invalid email addresses and might not work reliably during the demonstration.
- The team therefore changed the enhancement feature to in-app messaging.
- In-app messaging also required more work than expected, including message storage, backend APIs, conversation pages, and frontend updates.
- In future sprints, the team should check technical requirements and possible blockers before selecting a feature.

## Things to Change

- Update GitHub issue status as soon as the task status changes.
- Move completed issues to **Done** on time.
- Review the GitHub Project board during each Scrum meeting.
- Select enhancement features earlier.
- Set a time limit for feature discussions.
- Divide large features into smaller user stories.
- Check technical blockers before starting implementation.
- Keep recording important decisions in ADRs.

