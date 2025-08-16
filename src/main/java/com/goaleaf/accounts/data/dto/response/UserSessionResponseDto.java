package com.goaleaf.accounts.data.dto.response;

import com.github.pplociennik.commons.dto.BaseAbstractExtendableDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Represents a data transfer object (DTO) for user session details.
 * <p>
 * This class provides information about a user's session within the application, such as
 * the session ID, IP address, session start and last access times, as well as the location
 * and device used during the session. This data is typically returned in response to client
 * requests or used for tracking and monitoring session activity.
 * </p>
 *
 * <p>Instances of this DTO are intended to be immutable once constructed.</p>
 *
 * @author Created by: Pplociennik at 01.04.2025 21:54
 */
@EqualsAndHashCode( callSuper = true )
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSessionResponseDto extends BaseAbstractExtendableDto {

    /**
     * The unique identifier for the user's session.
     */
    private String id;

    /**
     * The IP address from which the user accessed this session.
     */
    private String ipAddress;

    /**
     * The timestamp (in milliseconds) marking the start of the session.
     */
    private Long start;

    /**
     * The timestamp (in milliseconds) representing the last time the session was accessed.
     */
    private Long lastAccess;

    /**
     * The approximate geographical location of the user during the session (if available).
     */
    private String location;

    /**
     * The type or identification of the device used to interact with the application during the session.
     */
    private String device;

}
