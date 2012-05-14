class PhotoAlbumJoin < ActiveRecord::Base
  belongs_to :photo
  belongs_to :album

  validates :photo_id, :album_id, :presence => true
end
