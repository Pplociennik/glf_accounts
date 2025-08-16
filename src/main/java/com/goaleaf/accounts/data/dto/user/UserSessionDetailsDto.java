package com.goaleaf.accounts.data.dto.user;

import com.github.pplociennik.commons.dto.BaseAbstractExtendableDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * A data transfer object representing the details of a user's session.
 * This class encapsulates information related to the user's session, including
 * session identifiers, timestamps, activity status, and metadata about the user's
 * location and device during the session.
 * <p>
 * Typically, this DTO is used to transfer session-related data between different
 * layers of the application, such as from the persistence layer to the service
 * or presentation layer.
 * <p>
 * It extends the {@code BaseAbstractExtendableDto} to provide extendable capabilities
 * for customizing and adding properties as needed for specific use cases.
 */
@EqualsAndHashCode( callSuper = true )
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserSessionDetailsDto extends BaseAbstractExtendableDto {

    private String id;

    private String sessionId;

    private String refreshToken;

    private String authenticatedUserId;

    private String location;

    private String device;
}
